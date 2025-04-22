package com.example.Movie.API.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "performer")
public class Performer extends AbstractEntity<Long> {
  private String fullName;
  private Date birthday;
  private boolean gender;
  private String country;
  private String description;
  private String avatar;
}
