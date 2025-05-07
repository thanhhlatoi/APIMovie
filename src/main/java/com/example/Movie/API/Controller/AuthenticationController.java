package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.*;
import com.example.Movie.API.DTO.Response.*;
import com.example.Movie.API.Service.Impl.AuthenticationService;
import com.example.Movie.API.Service.Impl.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final EmailService emailService;

  // Đăng Ký Người Dùng
  @PostMapping("/register")
  public ResponseEntity<?> register(
          @RequestBody @Valid RegistrationRequest request) {
    try {
      // Thực hiện đăng ký
      RegistrationResponse registrationResponse = authenticationService.register(request);

      // Tạo response với thêm thông tin để test
      Map<String, Object> response = new HashMap<>();
      response.put("registration", registrationResponse);

      // Nếu đăng ký thành công, gửi email xác thực
      if (registrationResponse.getUserId() != null) {
        CompletableFuture<String> emailFuture = emailService.sendVerificationEmail(request.getEmail());
        String verificationCode = emailFuture.get();
        response.put("verificationCode", verificationCode);
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Lỗi đăng ký: " + e.getMessage());
    }
  }

  // Đăng Nhập
  @PostMapping("/login")
  public ResponseEntity<?> login(
          @RequestBody @Valid AuthenticationRequest request,
          BindingResult bindingResult) {
    try {
      JwtAuthenticationResponse loginResponse = authenticationService.login(request, bindingResult);

      // Tạo response với thêm thông tin để test
      Map<String, Object> response = new HashMap<>();
      response.put("authentication", loginResponse);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Lỗi đăng nhập: " + e.getMessage());
    }
  }

  // Xác Minh Tài Khoản
  @PostMapping("/verify")
  public ResponseEntity<?> verifyAccount(
          @RequestBody @Valid VerificationRequest request) {
    try {
      VerificationResponse verificationResponse = authenticationService.verifyAccount(request);

      // Tạo response với thêm thông tin để test
      Map<String, Object> response = new HashMap<>();
      response.put("verification", verificationResponse);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Lỗi xác minh: " + e.getMessage());
    }
  }

  // Gửi Lại Mã Xác Minh
  @PostMapping("/resend-verification")
  public ResponseEntity<?> resendVerificationCode(
          @RequestBody @Valid ResendVerificationRequest request) {
    try {
      // Thực hiện gửi lại mã xác minh
      VerificationResponse verificationResponse = authenticationService.resendVerificationCode(request);

      // Gửi email xác thực
      CompletableFuture<String> emailFuture = emailService.sendVerificationEmail(request.getEmail());
      String verificationCode = emailFuture.get();

      // Tạo response với thêm thông tin để test
      Map<String, Object> response = new HashMap<>();
      response.put("verification", verificationResponse);
      response.put("verificationCode", verificationCode);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Lỗi gửi lại mã xác minh: " + e.getMessage());
    }
  }

  // Quên Mật Khẩu
  @PostMapping("/forgot-password")
  public ResponseEntity<?> requestPasswordReset(
          @RequestBody @Valid PasswordResetRequest request) {
    try {
      // Thực hiện yêu cầu đặt lại mật khẩu
      PasswordResetResponse resetResponse = authenticationService.requestPasswordReset(request);

      // Gửi email đặt lại mật khẩu
      CompletableFuture<String> emailFuture = emailService.sendPasswordResetEmail(request.getEmail());
      String resetToken = emailFuture.get();

      // Tạo response với thêm thông tin để test
      Map<String, Object> response = new HashMap<>();
      response.put("passwordReset", resetResponse);
      response.put("resetToken", resetToken);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Lỗi yêu cầu đặt lại mật khẩu: " + e.getMessage());
    }
  }

  // Đặt Lại Mật Khẩu
  @PostMapping("/reset-password")
  public ResponseEntity<PasswordResetResponse> resetPassword(
          @RequestBody @Valid NewPasswordRequest request) {
    return ResponseEntity.ok(authenticationService.resetPassword(request));
  }

  // Làm Mới Token
  @PostMapping("/refresh-token")
  public ResponseEntity<TokenRefreshResponse> refreshToken(
          @RequestBody @Valid TokenRefreshRequest request) {
    return ResponseEntity.ok(authenticationService.refreshToken(request));
  }

  // Kiểm Tra Token
  @PostMapping("/introspect")
  public ResponseEntity<IntrospectResponse> introspectToken(
          @RequestBody @Valid IntrospectRequest request) {
    return ResponseEntity.ok(authenticationService.introspect(request));
  }

  // Đăng Xuất
  @PostMapping("/logout")
  public ResponseEntity<LogoutResponse> logout(
          @RequestBody @Valid LogoutRequest request) {
    return ResponseEntity.ok(authenticationService.logout(request));
  }
}