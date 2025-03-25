package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User,Long> {
  Optional<User> findByEmail(String email);
  // kiem tra xem da co email hay chua
  boolean existsByEmail(String email);

  @EntityGraph(attributePaths = {"roles"})
  List<User> findAll();
}
