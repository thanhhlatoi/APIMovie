package com.example.Movie.API.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "User")
public class User extends AbstractEntity<Long> {
  private String firstname;
  private String lastname;
  private String fullName;
  private String email;
  private String password;
  private String phoneNumber;
  private Date dateOfBirth;
  private String address;
  private boolean gender;
  private String profilePictureUrl;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="User_Role",joinColumns = @JoinColumn(name="UserId"),inverseJoinColumns = @JoinColumn(name="roleName"))
  Set<Role> roles = new HashSet<>();
  @Column(nullable = false)
  private boolean verified;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
