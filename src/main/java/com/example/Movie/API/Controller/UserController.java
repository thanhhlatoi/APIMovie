package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.UserCreateRequest;
import com.example.Movie.API.DTO.Request.UserUpdateRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private UserService userService;
  @PostMapping("/createUser")
  public ResponseEntity<Object> createUserProfile(@RequestBody UserCreateRequest request){
        return ResponseBuilder.create().body(userService.createUser(request)).status(HttpStatus.OK).build();
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateUserProfile(@PathVariable long id, @ModelAttribute UserUpdateRequest request){
    return ResponseBuilder.create().body(userService.updateUser(id, request)).status(HttpStatus.OK).build();
  }
  @GetMapping("/{id}")
  public ResponseEntity<Object> getUserProfile(@PathVariable long id){
    return ResponseBuilder.create().body(userService.getUserById(id)).status(HttpStatus.OK).build();
  }
  @GetMapping
  public ResponseEntity<Object> getAllCategory() {
    return ResponseBuilder.create().body(userService.getAllUser()).status(HttpStatus.OK).build();
  }



}
