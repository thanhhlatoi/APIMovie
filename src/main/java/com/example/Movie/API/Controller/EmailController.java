package com.example.Movie.API.Controller;

import com.example.Movie.API.Service.Impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
  private final EmailService emailService;

  /**
   * Gửi mã xác thực
   */
  @PostMapping("/send-verification")
  public ResponseEntity<?> sendVerificationEmail(@RequestParam String email) {
    try {
      CompletableFuture<String> future = emailService.sendVerificationEmail(email);
      String verificationCode = future.get(); // Đợi và lấy mã xác thực

      // Trả về mã xác thực để test (chỉ dùng cho môi trường phát triển)
      Map<String, String> response = new HashMap<>();
      response.put("message", "Mã xác thực đã được gửi");
      response.put("verificationCode", verificationCode);
      response.put("email", email);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
              "message", "Lỗi khi gửi email",
              "error", e.getMessage(),
              "email", email
      ));
    }
  }

  /**
   * Gửi email đặt lại mật khẩu
   */
  @PostMapping("/send-password-reset")
  public ResponseEntity<?> sendPasswordResetEmail(@RequestParam String email) {
    try {
      CompletableFuture<String> future = emailService.sendPasswordResetEmail(email);
      String resetToken = future.get(); // Đợi và lấy token đặt lại mật khẩu

      // Trả về token để test (chỉ dùng cho môi trường phát triển)
      Map<String, String> response = new HashMap<>();
      response.put("message", "Email đặt lại mật khẩu đã được gửi");
      response.put("resetToken", resetToken);
      response.put("email", email);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
              "message", "Lỗi khi gửi email đặt lại mật khẩu",
              "error", e.getMessage(),
              "email", email
      ));
    }
  }

  /**
   * Endpoint tạo mã xác thực không gửi email (dùng cho test)
   */
  @GetMapping("/generate-verification-code")
  public ResponseEntity<Map<String, String>> generateVerificationCode() {
    String verificationCode = emailService.generateVerificationCode();
    return ResponseEntity.ok(Map.of(
            "verificationCode", verificationCode,
            "message", "Mã xác thực được tạo"
    ));
  }

  /**
   * Endpoint tạo token đặt lại mật khẩu không gửi email (dùng cho test)
   */
  @GetMapping("/generate-reset-token")
  public ResponseEntity<Map<String, String>> generateResetToken() {
    String resetToken = emailService.generateResetToken();
    return ResponseEntity.ok(Map.of(
            "resetToken", resetToken,
            "message", "Token đặt lại mật khẩu được tạo"
    ));
  }
}