package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "author")
public class Author extends AbstractEntity<Long>{
  private String fullName;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date birthday;
  private boolean gender;
  private String country;
  private String description;
  private String avatar;
}
