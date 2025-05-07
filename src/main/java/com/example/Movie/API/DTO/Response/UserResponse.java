package com.example.Movie.API.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  private long id;
  private String email;
  private String fullName;
  private Set<RoleResponse> roles;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private Date dateOfBirth;
  private String address;
  private boolean gender;
  private String profilePictureUrl;
  List<FavoriteResponse> favoritesResponses;
  private boolean verified;
  private boolean active;
}
