package com.example.Movie.API.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
  private Long userId;
  private String email;
  private String token;
  private String refreshToken;
  private String message;
  private boolean authenticated;
}
