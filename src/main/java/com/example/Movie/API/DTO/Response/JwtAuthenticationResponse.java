package com.example.Movie.API.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthenticationResponse {
  private String token;
  private String refreshToken;
  private UserResponse user;
  private long expiresIn;
  private String tokenType;
}
