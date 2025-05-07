package com.example.Movie.API.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Review")
@Entity
public class Review extends AbstractEntity<Long> {
  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "movieProductId", nullable = false)
  private MovieProduct movieProduct;

  @Column(nullable = false)
  private Integer rating;

  @Lob
  private String comment;

  private LocalDateTime createdAt = LocalDateTime.now();


}
