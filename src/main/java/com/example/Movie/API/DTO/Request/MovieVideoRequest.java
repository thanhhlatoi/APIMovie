package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieVideoRequest {
  private MultipartFile fileVideo;
  private Long movieProductId;
}
