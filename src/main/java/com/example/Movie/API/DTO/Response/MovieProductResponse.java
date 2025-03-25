package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Entity.Category;
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
  private String title;
  private String description;
  private int likes;
  private int dislikes;
  private String time;
  private String year;
  private int views;
  private String imgMovie;
  private Category category;
  private Set<AuthorResponse> author;
  private Set<PerformerResponse> performer;
}
