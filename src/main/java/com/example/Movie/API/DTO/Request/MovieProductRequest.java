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
  private int likes;
  private int dislikes;
  private int views;
  private MultipartFile image;
  private Long genreId;
  private Long authorId ;
  private Long categoryId;
  private Set<Long> performer ;
}
