package com.example.Movie.API.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class InvalidatedToken {
  @Id
  private String id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "invalidated_at")
  private LocalDateTime invalidatedAt;
}
