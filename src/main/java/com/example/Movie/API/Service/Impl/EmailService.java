package com.example.Movie.API.Service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${app.verification.code.expiry:300}") // 5 minutes default
  private int verificationCodeExpiry;

  /**
   * Gửi email xác thực với mã OTP
   */
  @Async
  public CompletableFuture<String> sendVerificationEmail(String toEmail) {
    try {
      // Tạo mã xác thực ngẫu nhiên 6 số
      String verificationCode = generateVerificationCode();

      // Gửi email
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(toEmail);
      helper.setSubject("Mã xác thực tài khoản Movie App");
      helper.setText("Mã xác thực của bạn là: " + verificationCode +
              "\nMã này sẽ hết hạn sau " + (verificationCodeExpiry / 60) + " phút.");

      mailSender.send(message);
      log.info("Đã gửi email xác thực đến: {}", toEmail);

      return CompletableFuture.completedFuture(verificationCode);
    } catch (MessagingException e) {
      log.error("Lỗi khi gửi email xác thực đến {}: {}", toEmail, e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Gửi email chào mừng
   */
  @Async
  public CompletableFuture<Void> sendWelcomeEmail(String toEmail, String fullName) {
    try {
      // Gửi email
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(toEmail);
      helper.setSubject("Chào mừng bạn đến với Movie App");
      helper.setText("Xin chào " + fullName + ",\n\n" +
              "Chúc mừng bạn đã xác minh thành công tài khoản trên Movie App!\n" +
              "Bây giờ bạn có thể trải nghiệm toàn bộ tính năng của ứng dụng.\n\n" +
              "Trân trọng,\n" +
              "Đội ngũ Movie App");

      mailSender.send(message);
      log.info("Đã gửi email chào mừng đến: {}", toEmail);

      return CompletableFuture.completedFuture(null);
    } catch (MessagingException e) {
      log.error("Lỗi khi gửi email chào mừng đến {}: {}", toEmail, e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Tạo mã xác thực ngẫu nhiên 6 số
   */
  public String generateVerificationCode() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(999999));
  }

  /**
   * Gửi email đặt lại mật khẩu
   */
  @Async
  public CompletableFuture<String> sendPasswordResetEmail(String toEmail) {
    try {
      // Tạo mã token đặt lại mật khẩu
      String resetToken = generateResetToken();

      // Gửi email
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(toEmail);
      helper.setSubject("Yêu cầu đặt lại mật khẩu Movie App");
      helper.setText("Mã đặt lại mật khẩu của bạn là: " + resetToken +
              "\nMã này sẽ hết hạn sau 24 giờ.");

      mailSender.send(message);
      log.info("Đã gửi email đặt lại mật khẩu đến: {}", toEmail);

      return CompletableFuture.completedFuture(resetToken);
    } catch (MessagingException e) {
      log.error("Lỗi khi gửi email đặt lại mật khẩu đến {}: {}", toEmail, e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Tạo token đặt lại mật khẩu
   */
  public String generateResetToken() {
    return java.util.UUID.randomUUID().toString();
  }
}