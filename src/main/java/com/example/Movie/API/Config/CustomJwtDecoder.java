package com.example.Movie.API.Config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

  @Value("${spring.jwt.signer-key:XIUMOY1zMZppH8gTKyXtLu7PJjG5zbI5N8vq28HbQFwO4+Av619325j6k9F3rMOG4pKwH0DIYYmSDorCex882/I3fz+vT4QDoF7YphgGm1J6O34bJJUqi0ZUBJtOzWw8}")
  private String signerKey;

  @Override
  public Jwt decode(String token) throws JwtException {
    try {
      // Log nguyên token nhận được
      log.info("Original token received: {}", token);

      // Check if token is undefined or null
      if (token == null || token.isEmpty() || "undefined".equals(token)) {
        log.info("No token provided or token is undefined - creating anonymous token");

        // Create a placeholder JWT for anonymous users
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "anonymous");
        claims.put("role", "ROLE_ANONYMOUS");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600); // 1 hour

        return Jwt.withTokenValue("anonymous-token")
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .issuedAt(now)
                .expiresAt(exp)
                .subject("anonymous")
                .build();
      }

      // Kiểm tra và loại bỏ tiền tố "Bearer " nếu có
      if (token.startsWith("Bearer ")) {
        token = token.substring(7).trim();
      }

      // Parse token
      SignedJWT signedJWT = SignedJWT.parse(token);

      // Verify signature
      JWSVerifier verifier = new MACVerifier(signerKey.getBytes(StandardCharsets.UTF_8));
      boolean verified = signedJWT.verify(verifier);

      if (!verified) {
        log.error("JWT signature verification failed");
        throw new JwtException("JWT signature verification failed");
      }

      // Extract claims
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      // Check expiration
      Date expirationTime = claimsSet.getExpirationTime();
      if (expirationTime != null && expirationTime.before(new Date())) {
        log.error("JWT token has expired");
        throw new JwtException("JWT token has expired");
      }

      // Headers and claims
      Map<String, Object> headers = new HashMap<>(signedJWT.getHeader().toJSONObject());
      Map<String, Object> claims = new HashMap<>(claimsSet.getClaims());

      // Build the Jwt
      Jwt jwt = Jwt.withTokenValue(token)
              .headers(h -> h.putAll(headers))
              .claims(c -> c.putAll(claims))
              .issuedAt(claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toInstant() : null)
              .expiresAt(claimsSet.getExpirationTime() != null ? claimsSet.getExpirationTime().toInstant() : null)
              .subject(claimsSet.getSubject())
              .build();

      log.info("JWT successfully decoded");
      return jwt;

    } catch (ParseException e) {
      log.error("Lỗi phân tích token: {}", e.getMessage(), e);

      // Create anonymous token instead of throwing exception
      Map<String, Object> headers = new HashMap<>();
      headers.put("alg", "HS256");

      Map<String, Object> claims = new HashMap<>();
      claims.put("sub", "anonymous");
      claims.put("role", "ROLE_ANONYMOUS");

      Instant now = Instant.now();
      Instant exp = now.plusSeconds(3600); // 1 hour

      return Jwt.withTokenValue("anonymous-token")
              .headers(h -> h.putAll(headers))
              .claims(c -> c.putAll(claims))
              .issuedAt(now)
              .expiresAt(exp)
              .subject("anonymous")
              .build();
    } catch (Exception e) {
      log.error("Lỗi giải mã token: {}", e.getMessage(), e);

      // Create anonymous token instead of throwing exception
      Map<String, Object> headers = new HashMap<>();
      headers.put("alg", "HS256");

      Map<String, Object> claims = new HashMap<>();
      claims.put("sub", "anonymous");
      claims.put("role", "ROLE_ANONYMOUS");

      Instant now = Instant.now();
      Instant exp = now.plusSeconds(3600); // 1 hour

      return Jwt.withTokenValue("anonymous-token")
              .headers(h -> h.putAll(headers))
              .claims(c -> c.putAll(claims))
              .issuedAt(now)
              .expiresAt(exp)
              .subject("anonymous")
              .build();
    }
  }
}