package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
  private String email;
  private String password;
  private String fullName;
  private Set<String> role;
}
