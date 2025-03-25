package com.example.Movie.API.Controller;

import com.example.Movie.API.Service.Impl.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailSend {
  @Autowired
  private EmailService emailService;
  @Autowired
  private JavaMailSender mailSender;
  @PostMapping("/send")
  public ResponseEntity<String> sendEmail(@RequestParam String toEmail) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(toEmail);
      helper.setSubject("Test Email");
      helper.setText("This is a test email from Spring Boot.");

      mailSender.send(message);

      return ResponseEntity.ok("Email sent successfully to " + toEmail);
    } catch (Exception e) {
      e.printStackTrace(); // Log lỗi chi tiết
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
    }
  }
}
