package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
  int deleteByExpiryDateBefore(Date date);
}
