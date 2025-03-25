package com.example.Movie.API.Config;

import com.example.Movie.API.Entity.Role;
import com.example.Movie.API.Entity.RoleManager;
import com.example.Movie.API.Entity.User;
import com.example.Movie.API.Repository.RoleRepository;
import com.example.Movie.API.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (!roleRepository.findByName(RoleManager.ADMIN.name()).isPresent()) {
      Role adminRole = new Role();
      adminRole.setName(RoleManager.ADMIN.name());
      roleRepository.save(adminRole);
    }

    if (!roleRepository.findByName(RoleManager.USERS.name()).isPresent()) {
      Role userRole = new Role();
      userRole.setName(RoleManager.USERS.name());
      roleRepository.save(userRole);
    }
    // Tạo admin mặc định
    if (!usersRepository.existsByEmail("admin@gmail.com")) {
      User adminUser = new User();
      adminUser.setFullName("Default Admin");
      adminUser.setEmail("admin@gmail.com");
      adminUser.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mã hóa
      Set<Role> roles = new HashSet<>();
      Role adminRole = roleRepository.findByName(RoleManager.ADMIN.name())
              .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
      roles.add(adminRole);
      adminUser.setRoles(roles);

      usersRepository.save(adminUser);
      System.out.println("Admin user created: admin@example.com / admin123");
    }
  }
}
