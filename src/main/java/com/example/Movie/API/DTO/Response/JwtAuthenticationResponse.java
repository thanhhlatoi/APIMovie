package com.example.Movie.API.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthenticationResponse {
  private String token;
  private Object user;
}
