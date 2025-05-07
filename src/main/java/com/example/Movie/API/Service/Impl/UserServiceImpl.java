package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.UserCreateRequest;
import com.example.Movie.API.DTO.Request.UserUpdateRequest;
import com.example.Movie.API.DTO.Response.FavoriteResponse;
import com.example.Movie.API.DTO.Response.RoleResponse;
import com.example.Movie.API.DTO.Response.UserResponse;
import com.example.Movie.API.Entity.*;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Mapper.FavoriteMapper;
import com.example.Movie.API.Mapper.UserMapper;
import com.example.Movie.API.Repository.FavoriteRepository;
import com.example.Movie.API.Repository.RoleRepository;
import com.example.Movie.API.Repository.UsersRepository;
import com.example.Movie.API.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private UsersRepository usersRepository;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private MinioServiceImpl minioService;
  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private FavoriteMapper favoriteMapper;

  @Override
  public UserResponse createUser(UserCreateRequest request) {
    if (usersRepository.existsByEmail(request.getEmail())) {
      throw new NotFoundException("Email này đã tồn tại");
    }

    User user = userMapper.requestToEntity(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setFullName(user.getFullName());

    Set<Role> roles = resolveRoles(request.getRole());
    user.setRoles(roles);

    usersRepository.save(user);
    return userMapper.toDTO(user);
  }

  @Override
  public UserResponse updateUser(long id, UserUpdateRequest request) {
    User user = usersRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id: " + id));

    updateUserBasicInfo(user, request);
    updateUserAvatar(user, request.getFileAvatar());

    usersRepository.save(user);
    return userMapper.toDTO(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void deleteUser(long id) {
    User user = usersRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id: " + id));
    usersRepository.delete(user);
  }

  @PreAuthorize("hasRole('USERS')")
  @Override
  public UserResponse getMyInFor() {
    var context = SecurityContextHolder.getContext();
    String email = context.getAuthentication().getName();
    User user = usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("k tim thay"));

    UserResponse response = userMapper.toDTO(user);

    Set<RoleResponse> roles = user.getRoles()
            .stream()
            .map(role -> new RoleResponse(role.getName(),role.getDescription()))
            .collect(Collectors.toSet());

    response.setRoles(roles);
    // Trả về ResponseEntity
    return ResponseEntity.ok(response).getBody();
  }
//
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public List<UserResponse> getAllUser() {
    List<User> users = usersRepository.findAll();
    return users.stream().map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public UserResponse getUserById(long id) {
    User user = usersRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id: " + id));

    // Lấy danh sách yêu thích của người dùng và chuyển đổi sang DTO
    List<Favorite> favorites = favoriteRepository.findAllByUserId(user.getId());
    List<FavoriteResponse> favoriteResponses = favorites.stream()
            .map(favoriteMapper::toDTO)
            .toList();

    UserResponse userResponse = userMapper.toDTO(user);
    userResponse.setFavoritesResponses(favoriteResponses);

    return userResponse;
  }

  // set role cho User
  private Set<Role> resolveRoles(Set<String> strRoles) {
    Set<Role> roles = new HashSet<>();
    if (strRoles == null || strRoles.isEmpty()) {
      Role defaultRole = roleRepository.findByName(RoleManager.USERS.name())
              .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò USERS"));
      roles.add(defaultRole);
    } else {
      for (String role : strRoles) {
        Role r = roleRepository.findByName(role.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò: " + role));
        roles.add(r);
      }
    }
    return roles;
  }
  //Update User
  private void updateUserBasicInfo(User user, UserUpdateRequest request) {
    if (request.getFirstName() != null) {
      user.setFirstname(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastname(request.getLastName());
    }
    if (request.getFirstName() != null || request.getLastName() != null) {
      String firstName = request.getFirstName() != null ? request.getFirstName() : user.getFirstname();
      String lastName = request.getLastName() != null ? request.getLastName() : user.getLastname();
      user.setFullName(firstName + " " + lastName);
    }
    if (request.getPhoneNumber() != null) {
      user.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getDateOfBirth() != null) {
      user.setDateOfBirth(new java.sql.Date(request.getDateOfBirth().getTime()));
    }
    if (request.getAddress() != null) {
      user.setAddress(request.getAddress());
    }

    // Giới tính luôn có giá trị (boolean primitive)
    user.setGender(request.isGender());
  }
  //Update Avatar
  private void updateUserAvatar(User user, MultipartFile fileAvatar) {
    if (fileAvatar != null && !fileAvatar.isEmpty()) {
      // Sử dụng phương thức mới
      String avatarPath = minioService.uploadUserAvatar(fileAvatar);

      // Kiểm tra xem upload có thành công không
      if (avatarPath != null) {
        user.setProfilePictureUrl(avatarPath);
      }
    }
  }


}
