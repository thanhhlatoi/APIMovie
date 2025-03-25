package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Favorite")
public class  Favorite extends AbstractEntity<Long> {
  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "movieProductId", nullable = false)
  private MovieProduct movieProduct;
}
