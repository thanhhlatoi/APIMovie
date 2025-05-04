package com.example.Movie.API.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
  @NotBlank(message = "Refresh token không được để trống")
  private String refreshToken;
}
