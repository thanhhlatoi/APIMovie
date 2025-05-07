package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.MovieVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieVideoResponse {
  private Long id;
  private String videoFilm;
  private MovieProduct movieProduct;
  private LocalDateTime watchedAt;
}
