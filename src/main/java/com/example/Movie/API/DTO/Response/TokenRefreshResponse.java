package com.example.Movie.API.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private boolean success;
}
