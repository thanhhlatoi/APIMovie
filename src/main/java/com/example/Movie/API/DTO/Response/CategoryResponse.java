package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.MovieProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
  private String name;
  private boolean active = true;
  private MovieProduct movieProduct;
}
