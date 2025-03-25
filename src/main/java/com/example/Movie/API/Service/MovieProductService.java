package com.example.Movie.API.Service;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.MovieProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieProductService extends EntityService<MovieProductRequest, MovieProductResponse> {
  MovieProduct likeMovie(long id );
  MovieProductResponse dislikesMovie(long id );
}
