package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
  private String fullName;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date birthday;
  private boolean gender;
  private String country;
  private String description;
  private MultipartFile fileAvatar;
}
