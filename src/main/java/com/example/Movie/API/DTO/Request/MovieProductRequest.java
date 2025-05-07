package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieProductRequest {
  private String title;
  private String description;
  private String time;
  private String year;
  private MultipartFile image;
  private Set<Long> genre;
  private Long authorId ;
  private Long categoryId;
  private Set<Long> performer ;
}
