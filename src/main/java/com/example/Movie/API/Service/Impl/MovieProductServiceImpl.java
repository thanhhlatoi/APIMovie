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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    //Quan he 1 - N
    Genre genre = genreRepository.findById(request.getGenreId()).orElse(null);
    MovieProduct movieProduct = movieProductMapper.requestToEntity(request);
    movieProduct.setGenre(genre);
    Author author = authorRepository.findById(request.getAuthorId()).orElse(null);
     movieProduct = movieProductMapper.requestToEntity(request);
     movieProduct.setAuthor(author);
     Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
     movieProduct = movieProductMapper.requestToEntity(request);
     movieProduct.setCategory(category);

    // quan he N- N
    Set<Performer> performers = new HashSet<>();
    for(Long performerId : request.getPerformer()){
      Performer performer = performerRepository.findById(performerId).orElseThrow(() -> new NotFoundException("Not Found Performer"));
      performers.add(performer);
    }
    movieProduct.setPerformer(performers);
    //upload anh
    final String fileStr = "imgMovie/" + request.getImage().getOriginalFilename();
    minioService.upLoadFile(request.getImage(), fileStr);
    movieProduct.setImgMovie(fileStr);

    movieProductRepository.save(movieProduct);
    return movieProductMapper.toDTO(movieProduct);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public MovieProductResponse updateEntity(long id, MovieProductRequest entity) {
    MovieProduct movieProduct = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Movie Product Not Found"));

    // Update Genre nếu có
    if (entity.getGenreId() != null) {
      Genre genre = genreRepository.findById(entity.getGenreId())
              .orElseThrow(() -> new NotFoundException("Genre Not Found"));
      movieProduct.setGenre(genre);
    }

    // Quan hệ 1 - N với Author
    if (entity.getAuthorId() != null) {
      Author author = authorRepository.findById(entity.getAuthorId()).orElseThrow(() -> new NotFoundException("Author Not Found"));
      movieProduct.setAuthor(author);
    }

    // Quan hệ N - N với Performer
    if (entity.getPerformer() != null) {
      Set<Performer> performers = new HashSet<>();
      for (Long performerId : entity.getPerformer()) {
        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException("Performer Not Found"));
        performers.add(performer);
      }
      movieProduct.setPerformer(performers);
    }

    // Upload ảnh nếu có
    if (entity.getImage() != null && !entity.getImage().isEmpty()) {
      final String fileStr = "imgMovie/" + entity.getImage().getOriginalFilename();
      minioService.upLoadFile(entity.getImage(), fileStr);
      movieProduct.setImgMovie(fileStr);
    }

    // Cập nhật các field đơn khác (nội dung, tiêu đề, thời lượng, ...)
    movieProductMapper.updateEntity(entity, movieProduct);

    movieProductRepository.save(movieProduct);
    return movieProductMapper.toDTO(movieProduct);
  }


  @Override
  public void deleteEntity(long id) {
    MovieProduct movie = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not Found MovieProduct"));

    movie.setAuthor(null);
    movie.setPerformer(null);
    movieProductRepository.delete(movie);
  }

  @Override
  public Page<MovieProductResponse> getAll(Pagination pagination) {
    Page<MovieProduct> movieProduct = movieProductRepository.findAll(pagination);
    return movieProduct.map(movieProductMapper::toDTO);
  }

//  @Override
//  public List<MovieProductResponse> getAll() {
//    List<MovieProduct> movieProducts = movieProductRepository.findAll();
//    return movieProducts.stream().map(user -> {
//      return movieProductMapper.toDTO(user);
//    }).collect(Collectors.toList());
//  }

  @Override
  public MovieProductResponse getById(long id) {
    // Tìm MovieProduct theo id
    var movieProduct = movieProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MovieProduct not found"));

    // Tăng view (giả sử mỗi lần gọi hàm views +1)
    movieProduct.setViews(movieProduct.getViews() + 1);

    // Lưu thay đổi vào database
    var updatedMovieProduct = movieProductRepository.save(movieProduct);

    // Chuyển đổi sang DTO
    return movieProductMapper.toDTO(updatedMovieProduct);
  }


  @Override
  public MovieProduct likeMovie(long id) {
    MovieProduct movie = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Movie not found"));
    movie.setLikes(movie.getLikes() + 1);
    return movieProductRepository.save(movie);
  }

  @Override
  public MovieProductResponse dislikesMovie(long id) {
    MovieProduct movie = movieProductRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Movie not found"));
    movie.setDislikes(movie.getDislikes() + 1);
    movieProductRepository.save(movie);
    return movieProductMapper.toDTO(movie);
  }

  @Override
  public List<MovieProduct> searchMoviesByTitle(String title) {
    return movieProductRepository.findByTitleContaining(title);
  }

  @Override
  public MovieProductResponse getMovieProductWithVideo(long id) {
    MovieProduct movieProduct = movieProductRepository.findByIdWithMovieVideo(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy MovieProduct với id: " + id));
    return movieProductMapper.toDTO(movieProduct);
  }

}
