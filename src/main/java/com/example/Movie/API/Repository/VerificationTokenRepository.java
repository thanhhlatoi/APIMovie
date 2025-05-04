package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.User;
import com.example.Movie.API.Entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);

  Optional<VerificationToken> findByTokenAndTokenType(String token, String tokenType);

  void deleteByUser(User user);

  int deleteByExpiryDateBefore(Date date);
}
