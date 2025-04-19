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
    if (usersRepository.existsByEmail(request.getEmail())) throw new NotFoundException("Email này đã tồn tại");
    User user = userMapper.requestToEntity(request);
    //ma hoa mat khau
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setFullName(user.getFullName());
    //set role de phan quyen
    Set<String> strRoles = request.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(RoleManager.USERS.name())
              .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        if (role.equals("admin")) {
          Role adminRole = roleRepository.findByName(RoleManager.ADMIN.name())
                  .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò."));
          roles.add(adminRole);
        } else {
          Role userRole = roleRepository.findByName(RoleManager.USERS.name())
                  .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò."));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    usersRepository.save(user);
    return userMapper.toDTO(user);

  }

  @Override
  public UserResponse updateUser(long id, UserUpdateRequest request) {
    User user = usersRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id: " + id));

    // Cập nhật các trường của người dùng nếu được cung cấp trong yêu cầu
    if (request.getFirstName() != null) {
      user.setFirstname(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastname(request.getLastName());
    }
    // Cập nhật họ tên đầy đủ nếu tên hoặc họ được cập nhật
    if (request.getFirstName() != null || request.getLastName() != null) {
      String firstName = request.getFirstName() != null ? request.getFirstName() : user.getFirstname();
      String lastName = request.getLastName() != null ? request.getLastName() : user.getLastname();
      user.setFullName(firstName + " " + lastName);
    }
    if (request.getPhoneNumber() != null) {
      user.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getDateOfBirth() != null) {
      // Chuyển đổi java.util.Date sang java.sql.Date
      user.setDateOfBirth(new java.sql.Date(request.getDateOfBirth().getTime()));
    }
    if (request.getAddress() != null) {
      user.setAddress(request.getAddress());
    }
    // Giới tính là một boolean nguyên thủy, nên nó luôn được cung cấp
    user.setGender(request.isGender());

    // Cập nhật ảnh đại diện nếu được cung cấp
    if (request.getFileAvatar() != null && !request.getFileAvatar().isEmpty()) {
      final String fileStr = "user/" + request.getFileAvatar().getOriginalFilename();
      minioService.upLoadFile(request.getFileAvatar(), fileStr);
      user.setProfilePictureUrl(fileStr);
    }

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
}
