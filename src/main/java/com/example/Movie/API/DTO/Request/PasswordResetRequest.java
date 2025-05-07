package com.example.Movie.API.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Địa chỉ email không hợp lệ")
  private String email;
}
