package com.example.Movie.API.Service;

import com.example.Movie.API.DTO.Request.UserCreateRequest;
import com.example.Movie.API.DTO.Request.UserUpdateRequest;
import com.example.Movie.API.DTO.Response.UserResponse;

import java.util.List;

public interface UserService {
  UserResponse createUser(UserCreateRequest request);
  UserResponse updateUser(long id, UserUpdateRequest request);
  void deleteUser(long id);
  UserResponse getMyInFor();
  List<UserResponse> getAllUser();
  UserResponse getUserById(long id);
}
