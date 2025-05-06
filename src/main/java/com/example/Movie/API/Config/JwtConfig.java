package com.example.Movie.API.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

  @Value("${jwt.signerKey:XIUMOY1zMZppH8gTKyXtLu7PJjG5zbI5N8vq28HbQFwO4+Av619325j6k9F3rMOG4pKwH0DIYYmSDorCex882/I3fz+vT4QDoF7YphgGm1J6O34bJJUqi0ZUBJtOzWw8}")
  private String signerKey;

  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey key = new SecretKeySpec(signerKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
    return NimbusJwtDecoder.withSecretKey(key).build();
  }
}
