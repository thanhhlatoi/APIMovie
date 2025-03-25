package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MovieVideo")
public class MovieVideo extends AbstractEntity<Long> {
  private String urlVideo;
  private LocalDateTime watchedAt = LocalDateTime.now();
  @OneToOne
  @JoinColumn(name = "movieProductId")
  private MovieProduct movieProduct;

}
