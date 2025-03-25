package com.example.Movie.API.DTO.Request;

import com.example.Movie.API.Entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
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
  private long categoryId;
  private Set<Long> author ;
  private Set<Long> performer ;
}
