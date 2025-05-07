package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "genre")
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Genre extends AbstractEntity<Long> {
  private String name;
  private boolean active;
}
