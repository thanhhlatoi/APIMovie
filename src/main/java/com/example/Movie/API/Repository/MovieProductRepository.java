package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.MovieProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieProductRepository extends JpaRepository<MovieProduct, Long> {
  @Query("SELECT mp FROM MovieProduct mp LEFT JOIN FETCH mp.movieVideo WHERE mp.id = :id")
  Optional<MovieProduct> findByIdWithMovieVideo(@Param("id") Long id);


}
