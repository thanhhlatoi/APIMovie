package com.example.Movie.API.DTO.Request;

import com.example.Movie.API.Entity.MovieProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
  private String name;

}
