package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
  private String fullName;
  private Date birthday;
  private boolean gender;
  private String country;
  private String describe;
  private MultipartFile fileAvatar;
}
