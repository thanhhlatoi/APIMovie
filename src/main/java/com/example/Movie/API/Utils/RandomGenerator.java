package com.example.Movie.API.Utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Component
public class RandomGenerator {
  private final Random random;

  public RandomGenerator() {
    this.random = new SecureRandom(); // Sử dụng SecureRandom cho bảo mật tốt hơn
  }

  /**
   * Tạo mã xác thực ngẫu nhiên 6 số
   */
  public String generateVerificationCode() {
    return String.format("%06d", random.nextInt(999999));
  }

  /**
   * Tạo mã xác thực ngẫu nhiên với độ dài tùy chỉnh
   */
  public String generateVerificationCode(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Độ dài phải lớn hơn 0");
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(random.nextInt(10));
    }
    return sb.toString();
  }

  /**
   * Tạo token đặt lại mật khẩu
   */
  public String generateResetToken() {
    return UUID.randomUUID().toString();
  }

  /**
   * Tạo chuỗi ngẫu nhiên với độ dài tùy chỉnh
   */
  public String generateRandomString(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Độ dài phải lớn hơn 0");
    }

    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(chars.length());
      sb.append(chars.charAt(index));
    }
    return sb.toString();
  }

  /**
   * Tạo số ngẫu nhiên trong khoảng
   */
  public int generateRandomNumber(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException("Giá trị min phải nhỏ hơn max");
    }
    return random.nextInt(max - min + 1) + min;
  }

  /**
   * Tạo chuỗi ngẫu nhiên cho mã giảm giá
   */
  public String generatePromoCode(int length) {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Bỏ một số ký tự dễ nhầm lẫn
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(chars.length());
      sb.append(chars.charAt(index));
    }
    return sb.toString();
  }

  /**
   * Tạo chuỗi ngẫu nhiên với tiền tố
   */
  public String generateWithPrefix(String prefix, int length) {
    return prefix + generateRandomString(length);
  }
}
