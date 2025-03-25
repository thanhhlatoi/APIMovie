package com.example.Movie.API.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Value("${spring.jwt.signer-key}")
  private String signerKey;
  public  static final String[] PUBLIC_ENDPOINTS = {"/api/movieProduct/**","/api/category/**","/api/videos/**","/api/user/createUser"};
  @Autowired
  private CustomJwtDecoder jwtDecoder;

//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
//    httpSecurity.authorizeHttpRequests(request -> request
//            .requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINTS)
//            .permitAll()
//            .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS)
//            .permitAll()
//            .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINTS)
//            .permitAll()
//            .anyRequest()
//            .authenticated());
//
////    httpSecurity.oauth2ResourceServer(oauth2 ->
////            oauth2.jwt(jwtConfigurer ->
////                            jwtConfigurer.decoder(jwtDecoder)
////                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
////                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
////    );
//
//    httpSecurity.csrf(AbstractHttpConfigurer::disable);
//
//    return httpSecurity.build();
//  }
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//    httpSecurity.authorizeHttpRequests(request -> request
//            .anyRequest()
//            .permitAll()); // Cho phép tất cả các request
//
//    httpSecurity.csrf(AbstractHttpConfigurer::disable);
//
//    return httpSecurity.build();
//  }

//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//    httpSecurity
//            .csrf(AbstractHttpConfigurer::disable)
//            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Áp dụng CORS
//            .authorizeHttpRequests(request -> request
//                    .anyRequest().permitAll()); // Cho phép tất cả các request
//
//    return httpSecurity.build();
//  }

  @Bean

  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("*"); // Hoặc domain cụ thể
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.addExposedHeader("Content-Range");
    config.addExposedHeader("Accept-Ranges");
    config.addExposedHeader("Content-Length");
    config.addExposedHeader("Content-Type");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
//    httpSecurity.authorizeHttpRequests(request -> request
//            .requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINTS)
//            .permitAll()
//            .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS)
//            .permitAll()
//            .anyRequest()
//            .authenticated());

    httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Áp dụng CORS
            .authorizeHttpRequests(request -> request
                    .anyRequest().permitAll()); // Cho phép tất cả các request

    httpSecurity.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwtConfigurer ->
                            jwtConfigurer.decoder(jwtDecoder)
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
    );

    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    return httpSecurity.build();
  }

    @Bean
    PasswordEncoder passwordEncoder(){
      return new BCryptPasswordEncoder(10);
    }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter(){
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;

  }
}
