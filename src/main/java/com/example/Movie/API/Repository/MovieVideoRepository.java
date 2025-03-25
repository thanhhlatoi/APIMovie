package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.MovieVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieVideoRepository extends JpaRepository<MovieVideo, Long> {
  boolean existsByMovieProduct(MovieProduct movieProduct);
}
