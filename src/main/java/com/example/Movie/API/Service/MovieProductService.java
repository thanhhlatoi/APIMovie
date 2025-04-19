package com.example.Movie.API.Service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.MovieProduct;

@Repository
public interface MovieProductService extends EntityService<MovieProductRequest, MovieProductResponse> {
  MovieProduct likeMovie(long id );
  MovieProductResponse dislikesMovie(long id );
  List<MovieProduct> searchMoviesByTitle(String title);
  MovieProductResponse getMovieProductWithVideo(long id);
}
