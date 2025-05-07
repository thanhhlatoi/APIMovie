package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private Date dateOfBirth;
  private String address;
  private boolean gender;
  private MultipartFile fileAvatar;
}
