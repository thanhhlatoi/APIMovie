package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.MovieVideoRequest;
import com.example.Movie.API.DTO.Response.MovieVideoResponse;
import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.MovieVideo;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Exception.VideoProcessingException;
import com.example.Movie.API.Repository.MovieProductRepository;
import com.example.Movie.API.Repository.MovieVideoRepository;
import com.example.Movie.API.Service.MovieVideoService;
import com.example.Movie.API.Utils.Pagination;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieVideoServiceImpl implements MovieVideoService {
  private final MovieVideoRepository movieVideoRepository;
  private final HlsServiceImpl hlsService;
  private final MovieProductRepository movieProductRepository;

  @Override
  @Transactional
  public MovieVideoResponse createEntity(MovieVideoRequest request) {
    try {
      // Tìm MovieProduct
      MovieProduct movieProduct = findMovieProductById(request.getMovieProductId());

      // Kiểm tra xem MovieVideo đã tồn tại cho MovieProduct này chưa
      if (movieVideoRepository.existsByMovieProduct(movieProduct)) {
        throw new EntityExistsException("Video đã tồn tại cho phim này");
      }

      // Tạo MovieVideo mới
      MovieVideo movieVideo = new MovieVideo();

      // Upload file lên MinIO và chuyển đổi sang HLS
      String fileUrl = hlsService.uploadFile(request.getFileVideo());

      // Kiểm tra lỗi từ hlsService
      if (fileUrl.startsWith("ERROR:")) {
        throw new VideoProcessingException(fileUrl.substring(7));
      }

      // Thiết lập thông tin cho MovieVideo
      movieVideo.setUrlVideo(fileUrl);
      movieVideo.setMovieProduct(movieProduct);

      // Lưu vào database
      MovieVideo savedMovieVideo = movieVideoRepository.save(movieVideo);

      // Trả về response
      return buildMovieVideoResponse(savedMovieVideo);

    } catch (EntityExistsException | NotFoundException | VideoProcessingException e) {
      log.warn("Validation error in createEntity: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error in createEntity", e);
      throw new RuntimeException("Lỗi khi tạo video: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public MovieVideoResponse updateEntity(long id, MovieVideoRequest request) {
    try {
      // Tìm MovieVideo hiện tại
      MovieVideo movieVideo = findMovieVideoById(id);

      // Chỉ cập nhật video nếu có file mới
      if (request.getFileVideo() != null && !request.getFileVideo().isEmpty()) {
        String fileUrl = hlsService.uploadFile(request.getFileVideo());

        // Kiểm tra lỗi từ hlsService
        if (fileUrl.startsWith("ERROR:")) {
          throw new VideoProcessingException(fileUrl.substring(7));
        }

        movieVideo.setUrlVideo(fileUrl);
      }

      // Cập nhật MovieProduct nếu có thay đổi
      if (request.getMovieProductId() != null) {
        if (movieVideo.getMovieProduct() == null ||
                !request.getMovieProductId().equals(movieVideo.getMovieProduct().getId())) {
          MovieProduct movieProduct = findMovieProductById(request.getMovieProductId());
          movieVideo.setMovieProduct(movieProduct);
        }
      }

      // Lưu thay đổi
      MovieVideo updatedMovieVideo = movieVideoRepository.save(movieVideo);

      return buildMovieVideoResponse(updatedMovieVideo);

    } catch (NotFoundException | VideoProcessingException e) {
      log.warn("Validation error in updateEntity: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error in updateEntity", e);
      throw new RuntimeException("Lỗi khi cập nhật video: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public void deleteEntity(long id) {
    try {
      MovieVideo movieVideo = findMovieVideoById(id);
      movieVideoRepository.delete(movieVideo);
      log.info("Deleted MovieVideo with id: {}", id);
    } catch (NotFoundException e) {
      log.warn("VideoVideo not found with id: {}", id);
      throw e;
    } catch (Exception e) {
      log.error("Error deleting MovieVideo with id: {}", id, e);
      throw new RuntimeException("Lỗi khi xóa video: " + e.getMessage(), e);
    }
  }

  @Override
  public Page<MovieVideoResponse> getAll(Pagination pagination) {
    try {
      Page<MovieVideo> movieVideos = movieVideoRepository.findAll(pagination);
      return movieVideos.map(this::buildMovieVideoResponse);
    } catch (Exception e) {
      log.error("Error getting all MovieVideos", e);
      throw new RuntimeException("Lỗi khi lấy danh sách video: " + e.getMessage(), e);
    }
  }

  @Override
  public MovieVideoResponse getById(long id) {
    MovieVideo movieVideo = findMovieVideoById(id);
    return buildMovieVideoResponse(movieVideo);
  }



  /**
   * Helper method to find MovieProduct by ID
   */
  private MovieProduct findMovieProductById(Long id) {
    return movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy phim với id: " + id));
  }

  /**
   * Helper method to find MovieVideo by ID
   */
  private MovieVideo findMovieVideoById(Long id) {
    return movieVideoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy video với id: " + id));
  }

  /**
   * Helper method to build MovieVideoResponse
   */
  private MovieVideoResponse buildMovieVideoResponse(MovieVideo movieVideo) {
    return MovieVideoResponse.builder()
            .id(movieVideo.getId())
            .movieProduct(movieVideo.getMovieProduct())
            .videoFilm(movieVideo.getUrlVideo())
            .watchedAt(movieVideo.getWatchedAt())
            .build();
  }
}
