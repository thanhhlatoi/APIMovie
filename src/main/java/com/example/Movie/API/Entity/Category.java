package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@Table(name = "category")
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Category extends AbstractEntity<Long> {
  private String name;
  private boolean active;
}
