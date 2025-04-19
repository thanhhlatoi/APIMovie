package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.MovieVideoRequest;
import com.example.Movie.API.DTO.Response.MovieVideoResponse;
import com.example.Movie.API.Entity.MovieVideo;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Repository.MovieProductRepository;
import com.example.Movie.API.Repository.MovieVideoRepository;
import com.example.Movie.API.Service.MovieVideoService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MovieVideoServiceImpl implements MovieVideoService {
  @Autowired
  private MovieVideoRepository movieVideoRepository;
  @Autowired
  private HlsServiceImpl hlsService;
  @Autowired
  private MovieProductRepository movieProductRepository;

  @Override
  public MovieVideoResponse createEntity(MovieVideoRequest request) throws Exception {
    var movieProduct = movieProductRepository.findById(request.getMovieProductId())
            .orElseThrow(() -> new RuntimeException("MovieProduct not found"));

    if (movieVideoRepository.existsByMovieProduct(movieProduct)) throw new NotFoundException("This MovieId already exists");
    var movieVideo = new MovieVideo();
    // Upload file lên MinIO
    String fileUrl = hlsService.uploadFile(request.getFileVideo());
    movieVideo.setUrlVideo(fileUrl);
    movieVideo.setMovieProduct(movieProduct);
    // Lưu vào database
    var savedMovieVideo = movieVideoRepository.save(movieVideo);
    return MovieVideoResponse.builder()
            .movieProduct(savedMovieVideo.getMovieProduct())
            .videoFilm(savedMovieVideo.getUrlVideo())
            .build();
  }

  @Override
  public MovieVideoResponse updateEntity(long id, MovieVideoRequest entity) {
    return null;
  }

  @Override
  public void deleteEntity(long id) {

  }

  @Override
  public Page<MovieVideoResponse> getAll(Pagination pagination) {
    Page<MovieVideo> movieVideos = movieVideoRepository.findAll(pagination);
    return movieVideos.map(movieVideo -> MovieVideoResponse.builder()
            .movieProduct(movieVideo.getMovieProduct())
            .videoFilm(movieVideo.getUrlVideo())
            .build());
  }

//  @Override
//  public List<MovieVideoResponse> getAll() {
//    List<MovieVideo> users = movieVideoRepository.findAll();
//    return movieVideoRepository.findAll().stream()
//            .map(movie -> new MovieVideoResponse(movie.getUrlVideo(), movie.getMovieProduct(),movie.getWatchedAt()))
//            .collect(Collectors.toList());
//  }

  @Override
  public MovieVideoResponse getById(long id) {
    return null;
  }
}
