package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.*;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Mapper.MovieProductMapper;
import com.example.Movie.API.Repository.AuthorRepository;
import com.example.Movie.API.Repository.GenreRepository;
import com.example.Movie.API.Repository.MovieProductRepository;
import com.example.Movie.API.Repository.PerformerRepository;
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
  int i = 1;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public MovieProductResponse createEntity(MovieProductRequest request) {
    Genre genre = genreRepository.findById(request.getCategoryId()).orElse(null);
    MovieProduct movieProduct = movieProductMapper.requestToEntity(request);
    movieProduct.setGenre(genre);
    // quan he N- N
    Set<Author> authors = new HashSet<>();
    for(Long authorId : request.getAuthor()){
      Author author = authorRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Not Found Author"));
      authors.add(author);
    }
    movieProduct.setAuthor(authors);
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
    if (entity.getCategoryId() != null) {
      Genre genre = genreRepository.findById(entity.getCategoryId())
              .orElseThrow(() -> new NotFoundException("Genre Not Found"));
      movieProduct.setGenre(genre);
    }

    // Quan hệ N - N với Author
    if (entity.getAuthor() != null) {
      Set<Author> authors = new HashSet<>();
      for (Long authorId : entity.getAuthor()) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Author Not Found"));
        authors.add(author);
      }
      movieProduct.setAuthor(authors);
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
    // TODO Auto-generated method stub
    MovieProduct movieProduct = movieProductRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found MovieProduct"));
    movieProduct.getAuthor().remove(this);
    movieProduct.getPerformer().remove(this);
    movieProductRepository.delete(movieProduct);
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
    Optional<MovieProduct> movieOpt = movieProductRepository.findById(id);
    if (movieOpt.isPresent()) {
      var movieProduct = new MovieProduct();
      movieProduct.setLikes(movieProduct.getLikes() + i);

      return movieProductRepository.save(movieProduct);
    }
    throw new RuntimeException("Movie not found");
  }

  @Override
  public MovieProductResponse dislikesMovie(long id) {
    Optional<MovieProduct> movieOpt = movieProductRepository.findById(id);
    if (movieOpt.isPresent()) {
      var movieProduct = new MovieProduct();
      movieProduct.setDislikes(movieProduct.getDislikes() - 1);
      movieProductRepository.save(movieProduct);
      return movieProductMapper.toDTO(movieProduct);
    }
    throw new RuntimeException("Movie not found");
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
