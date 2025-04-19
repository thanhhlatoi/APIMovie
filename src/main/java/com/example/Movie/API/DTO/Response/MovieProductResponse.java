package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieProductResponse {
  private Long id;
  private String title;
  private String description;
  private int likes;
  private int dislikes;
  private String time;
  private String year;
  private int views;
  private String imgMovie;
  private Genre genre;
  private Set<AuthorResponse> author;
  private Set<PerformerResponse> performer;
}
