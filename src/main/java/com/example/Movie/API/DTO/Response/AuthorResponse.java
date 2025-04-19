package com.example.Movie.API.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponse {
  private String fullName;
  private Date birthday;
  private boolean gender;
  private String country;
  private String describe;
  private String avatar;
}
