package com.example.Movie.API.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "verificationTokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken extends AbstractEntity<Long>{
  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;

  @Column(name = "token_type", columnDefinition = "VARCHAR(255) DEFAULT 'VERIFICATION'")
  private String tokenType = "VERIFICATION";
}
