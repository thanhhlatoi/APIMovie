package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.*;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Mapper.MovieProductMapper;
import com.example.Movie.API.Repository.*;
import com.example.Movie.API.Service.MovieProductService;

import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieProductServiceImpl implements MovieProductService {
  @Autowired
  private MovieProductRepository movieProductRepository;
  @Autowired
  private MovieProductMapper movieProductMapper;
  @Autowired
  private MinioServiceImpl minioService;
  @Autowired
  private GenreRepository genreRepository;
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private PerformerRepository performerRepository;
  @Autowired
  private CategoryRepository categoryRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public MovieProductResponse createEntity(MovieProductRequest request) {
    MovieProduct movieProduct = movieProductMapper.requestToEntity(request);
    setRelationships(movieProduct, request);
    handleImageUpload(movieProduct, request.getImage());
    movieProductRepository.save(movieProduct);
    return movieProductMapper.toDTO(movieProduct);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public MovieProductResponse updateEntity(long id, MovieProductRequest request) {
    MovieProduct movieProduct = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Movie Product Not Found"));

    setRelationships(movieProduct, request);
    handleImageUpload(movieProduct, request.getImage());
    movieProductMapper.updateEntity(request, movieProduct);

    movieProductRepository.save(movieProduct);
    return movieProductMapper.toDTO(movieProduct);
  }

  // Trích xuất các quan hệ vào phương thức riêng để tái sử dụng
  private void setRelationships(MovieProduct movieProduct, MovieProductRequest request) {
    // Xử lý quan hệ với Category - cho phép null
    setCategoryRelationship(movieProduct, request.getCategoryId());

    // Xử lý quan hệ với Author
    setAuthorRelationship(movieProduct, request.getAuthorId());

    // Xử lý quan hệ N-N với Genre
    setGenreRelationships(movieProduct, request.getGenre());

    // Xử lý quan hệ N-N với Performer
    setPerformerRelationships(movieProduct, request.getPerformer());
  }

  private void setCategoryRelationship(MovieProduct movieProduct, Long categoryId) {
    if (categoryId != null) {
      Category category = categoryRepository.findById(categoryId)
              .orElseThrow(() -> new NotFoundException("Not Found Category"));
      movieProduct.setCategory(category);
    }
    // Không cần else vì mặc định đã là null
  }

  private void setAuthorRelationship(MovieProduct movieProduct, Long authorId) {
    if (authorId != null) {
      Author author = authorRepository.findById(authorId)
              .orElseThrow(() -> new NotFoundException("Not Found Author"));
      movieProduct.setAuthor(author);
    }
  }

  private void setGenreRelationships(MovieProduct movieProduct, Set<Long> genreIds) {
    if (genreIds != null && !genreIds.isEmpty()) {
      Set<Genre> genres = new HashSet<>();
      for (Long genreId : genreIds) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Not Found Genre with ID: " + genreId));
        genres.add(genre);
      }
      movieProduct.setGenres(genres);
    }
  }

  private void setPerformerRelationships(MovieProduct movieProduct, Set<Long> performerIds) {
    if (performerIds != null && !performerIds.isEmpty()) {
      Set<Performer> performers = new HashSet<>();
      for (Long performerId : performerIds) {
        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException("Not Found Performer with ID: " + performerId));
        performers.add(performer);
      }
      movieProduct.setPerformers(performers);
    }
  }

  private void handleImageUpload(MovieProduct movieProduct, MultipartFile image) {
    if (image != null && !image.isEmpty()) {
      final String fileStr = "imgMovie/" + image.getOriginalFilename();
      minioService.upLoadFile(image, fileStr);
      movieProduct.setImgMovie(fileStr);
    }
  }

  @Override
  public void deleteEntity(long id) {
    MovieProduct movie = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not Found MovieProduct"));

    // Xóa các quan hệ trước khi xóa entity để tránh lỗi khóa ngoại
    movie.setAuthor(null);
    movie.setGenres(null);
    movie.setPerformers(null);
    movie.setCategory(null);

    movieProductRepository.delete(movie);
  }

  @Override
  public Page<MovieProductResponse> getAll(Pagination pagination) {
    return movieProductRepository.findAll(pagination)
            .map(movieProductMapper::toDTO);
  }

  @Override
  public MovieProductResponse getById(long id) {
    MovieProduct movieProduct = findMovieProductById(id);
    movieProduct.setViews(movieProduct.getViews() + 1);
    return movieProductMapper.toDTO(movieProductRepository.save(movieProduct));
  }

  @Override
  public MovieProduct likeMovie(long id) {
    MovieProduct movie = findMovieProductById(id);
    movie.setLikes(movie.getLikes() + 1);
    return movieProductRepository.save(movie);
  }

  @Override
  public MovieProductResponse dislikesMovie(long id) {
    MovieProduct movie = findMovieProductById(id);
    movie.setDislikes(movie.getDislikes() + 1);
    return movieProductMapper.toDTO(movieProductRepository.save(movie));
  }

  private MovieProduct findMovieProductById(long id) {
    return movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Movie not found"));
  }

  @Override
  public List<MovieProduct> searchMoviesByTitle(String title) {
    if (title == null || title.isEmpty()) {
      return List.of(); // Trả về danh sách rỗng thay vì truy vấn không cần thiết
    }
    return movieProductRepository.findByTitleContaining(title);
  }

  @Override
  public MovieProductResponse getMovieProductWithVideo(long id) {
    return movieProductRepository.findByIdWithMovieVideo(id)
            .map(movieProductMapper::toDTO)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy MovieProduct với id: " + id));
  }

  @Override
  public List<MovieProduct> searchMoviesByYear(String year) {
    return movieProductRepository.findByYear(year);
  }

  @Override
  public List<MovieProduct> searchMoviesByGenre(Long genreId) {
    return movieProductRepository.findByGenreId(genreId);
  }

  @Override
  public List<MovieProduct> searchMoviesByCategory(Long categoryId) {
    return movieProductRepository.findByCategoryId(categoryId);
  }

  @Override
  public List<MovieProduct> searchMoviesByAuthor(Long authorId) {
    return movieProductRepository.findByAuthorId(authorId);
  }

  @Override
  public List<MovieProduct> searchMoviesByPerformer(Long performerId) {
    return movieProductRepository.findByPerformerId(performerId);
  }
}