package com.example.Movie.API.Config;

import com.example.Movie.API.Entity.Role;
import com.example.Movie.API.Entity.RoleManager;
import com.example.Movie.API.Entity.User;
import com.example.Movie.API.Entity.VerificationToken;
import com.example.Movie.API.Repository.RoleRepository;
import com.example.Movie.API.Repository.UsersRepository;
import com.example.Movie.API.Repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UsersRepository usersRepository;
  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    // Tạo role ADMIN nếu chưa tồn tại
    Role adminRole = roleRepository.findByName(RoleManager.ADMIN.name())
            .orElseGet(() -> {
              Role newRole = new Role();
              newRole.setName(RoleManager.ADMIN.name());
              return roleRepository.save(newRole);
            });

    // Tạo role USERS nếu chưa tồn tại
    Role userRole = roleRepository.findByName(RoleManager.USERS.name())
            .orElseGet(() -> {
              Role newRole = new Role();
              newRole.setName(RoleManager.USERS.name());
              return roleRepository.save(newRole);
            });

    // Kiểm tra và đảm bảo admin đã được xác minh (đặt NGOÀI điều kiện)
    usersRepository.findByEmail("admin@gmail.com").ifPresent(admin -> {
      if (!admin.isVerified()) {
        admin.setVerified(true);
        admin.setActive(true);
        usersRepository.save(admin);
        System.out.println("Existing admin account has been verified: admin@gmail.com");
      }
    });

    // Tạo admin mặc định nếu chưa tồn tại
    if (!usersRepository.existsByEmail("admin@gmail.com")) {
      User adminUser = new User();
      adminUser.setFullName("Default Admin");
      adminUser.setEmail("admin@gmail.com");
      adminUser.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mã hóa

      // Tự động xác minh tài khoản admin
      adminUser.setVerified(true);
      adminUser.setActive(true);

      Set<Role> roles = new HashSet<>();
      roles.add(adminRole);
      adminUser.setRoles(roles);

      User savedAdmin = usersRepository.save(adminUser);

      // Tạo verification token để đảm bảo tính nhất quán
      VerificationToken verificationToken = VerificationToken.builder()
              .token(UUID.randomUUID().toString()) // Tạo token ngẫu nhiên
              .user(savedAdmin)
              .expiryDate(LocalDateTime.now().plusDays(1)) // Token hết hạn sau 1 ngày
              .tokenType("ADMIN_VERIFICATION") // Thêm loại token để phân biệt
              .build();

      verificationTokenRepository.save(verificationToken);

      System.out.println("Admin user created and verified: admin@gmail.com / admin123");
    }
  }
}