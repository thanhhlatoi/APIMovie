package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.*;
import com.example.Movie.API.DTO.Response.*;
import com.example.Movie.API.Entity.InvalidatedToken;
import com.example.Movie.API.Entity.RoleManager;
import com.example.Movie.API.Entity.User;
import com.example.Movie.API.Entity.VerificationToken;
import com.example.Movie.API.Exception.InvalidTokenException;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Exception.TokenExpiredException;
import com.example.Movie.API.Exception.UserAlreadyExistsException;
import com.example.Movie.API.Mapper.UserMapper;
import com.example.Movie.API.Repository.InvalidatedTokenRepository;
import com.example.Movie.API.Repository.UsersRepository;
import com.example.Movie.API.Repository.VerificationTokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
  private final UsersRepository userRepository;
  private final InvalidatedTokenRepository invalidatedTokenRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserMapper userMapper;
  private final EmailService emailService;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

  @Value("${jwt.signerKey:XIUMOY1zMZppH8gTKyXtLu7PJjG5zbI5N8vq28HbQFwO4+Av619325j6k9F3rMOG4pKwH0DIYYmSDorCex882/I3fz+vT4QDoF7YphgGm1J6O34bJJUqi0ZUBJtOzWw8}")
  protected String signerKey;

  @Value("${jwt.accessToken.expiry:3600}")
  private long accessTokenExpirySeconds;

  @Value("${jwt.refreshToken.expiry:86400}")
  private long refreshTokenExpirySeconds;

  @Value("${app.verification.expiry:86400}")
  private long verificationExpirySeconds;

  /**
   * Xác thực người dùng và tạo token
   */
  public RegistrationResponse authenticate(RegistrationRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với email: " + request.getEmail()));

    // Kiểm tra mật khẩu
    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
    if (!authenticated) {
      throw new NotFoundException("Email hoặc mật khẩu không chính xác");
    }

    // Kiểm tra tài khoản đã xác minh chưa, bỏ qua kiểm tra cho admin
    if (!user.isVerified() && !isAdminUser(user)) {
      return RegistrationResponse.builder()
              .authenticated(false)
              .message("Tài khoản chưa được xác minh. Vui lòng kiểm tra email của bạn.")
              .build();
    }

    // Tạo token truy cập và token làm mới
    String accessToken = generateToken(user, false);
    String refreshToken = generateToken(user, true);

    return RegistrationResponse.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .authenticated(true)
            .message("Đăng nhập thành công")
            .build();
  }

  /**
   * Đăng ký người dùng mới
   */
  @Transactional
  public RegistrationResponse register(RegistrationRequest request) {
    // Kiểm tra email đã tồn tại chưa
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException("Email đã được sử dụng");
    }

    // Tạo người dùng mới
    User newUser = User.builder()
            .email(request.getEmail())
            .fullName(request.getFullName())
            .password(passwordEncoder.encode(request.getPassword()))
            .verified(false)
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

    // Lưu người dùng
    User savedUser = userRepository.save(newUser);

    // Tạo mã xác minh và gửi email
    CompletableFuture<String> emailFuture = emailService.sendVerificationEmail(request.getEmail());

    try {
      String verificationCode = emailFuture.join();

      // Lưu mã xác minh
      VerificationToken verificationToken = VerificationToken.builder()
              .token(verificationCode)
              .user(savedUser)
              .expiryDate(LocalDateTime.now().plus(verificationExpirySeconds, ChronoUnit.SECONDS))
              .build();

      verificationTokenRepository.save(verificationToken);

      return RegistrationResponse.builder()
              .userId(savedUser.getId())
              .email(savedUser.getEmail())
              .message("Đăng ký thành công. Vui lòng kiểm tra email để xác minh tài khoản.")
              .build();

    } catch (Exception e) {
      log.error("Lỗi khi gửi email xác minh: {}", e.getMessage());
      return RegistrationResponse.builder()
              .userId(savedUser.getId())
              .email(savedUser.getEmail())
              .message("Đăng ký thành công nhưng không thể gửi email xác minh. Vui lòng liên hệ hỗ trợ.")
              .build();
    }
  }

  /**
   * Xác minh tài khoản
   */
  @Transactional
  public VerificationResponse verifyAccount(VerificationRequest request) {
    // Tìm mã xác minh
    VerificationToken verificationToken = verificationTokenRepository.findByToken(request.getCode())
            .orElseThrow(() -> new InvalidTokenException("Mã xác minh không hợp lệ"));

    // Kiểm tra hết hạn
    if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new TokenExpiredException("Mã xác minh đã hết hạn");
    }

    // Cập nhật trạng thái người dùng
    User user = verificationToken.getUser();
    user.setVerified(true);
    userRepository.save(user);

    // Xóa mã xác minh đã sử dụng
    verificationTokenRepository.delete(verificationToken);

    // Gửi email chào mừng
    emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

    return VerificationResponse.builder()
            .verified(true)
            .message("Xác minh tài khoản thành công")
            .build();
  }

  /**
   * Làm mới mã xác minh
   */
  @Transactional
  public VerificationResponse resendVerificationCode(ResendVerificationRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với email: " + request.getEmail()));

    if (user.isVerified()) {
      return VerificationResponse.builder()
              .verified(true)
              .message("Tài khoản đã được xác minh trước đó")
              .build();
    }

    // Xóa mã xác minh cũ
    verificationTokenRepository.deleteByUser(user);

    // Tạo mã mới và gửi email
    CompletableFuture<String> emailFuture = emailService.sendVerificationEmail(request.getEmail());

    try {
      String verificationCode = emailFuture.join();

      // Lưu mã xác minh mới
      VerificationToken verificationToken = VerificationToken.builder()
              .token(verificationCode)
              .user(user)
              .expiryDate(LocalDateTime.now().plus(verificationExpirySeconds, ChronoUnit.SECONDS))
              .build();

      verificationTokenRepository.save(verificationToken);

      return VerificationResponse.builder()
              .verified(false)
              .message("Mã xác minh mới đã được gửi")
              .build();

    } catch (Exception e) {
      log.error("Lỗi khi gửi lại mã xác minh: {}", e.getMessage());
      throw new RuntimeException("Không thể gửi lại mã xác minh: " + e.getMessage());
    }
  }

  /**
   * Yêu cầu đặt lại mật khẩu
   */
  @Transactional
  public PasswordResetResponse requestPasswordReset(PasswordResetRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với email: " + request.getEmail()));

    // Gửi email đặt lại mật khẩu
    CompletableFuture<String> emailFuture = emailService.sendPasswordResetEmail(request.getEmail());

    try {
      String resetToken = emailFuture.join();

      // Lưu token đặt lại mật khẩu
      VerificationToken passwordResetToken = VerificationToken.builder()
              .token(resetToken)
              .user(user)
              .tokenType("PASSWORD_RESET")
              .expiryDate(LocalDateTime.now().plus(24, ChronoUnit.HOURS))
              .build();

      verificationTokenRepository.save(passwordResetToken);

      return PasswordResetResponse.builder()
              .success(true)
              .message("Email đặt lại mật khẩu đã được gửi")
              .build();

    } catch (Exception e) {
      log.error("Lỗi khi gửi email đặt lại mật khẩu: {}", e.getMessage());
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu: " + e.getMessage());
    }
  }

  /**
   * Đặt lại mật khẩu
   */
  @Transactional
  public PasswordResetResponse resetPassword(NewPasswordRequest request) {
    // Tìm token đặt lại mật khẩu
    VerificationToken resetToken = verificationTokenRepository.findByTokenAndTokenType(request.getToken(), "PASSWORD_RESET")
            .orElseThrow(() -> new InvalidTokenException("Token đặt lại mật khẩu không hợp lệ"));

    // Kiểm tra hết hạn
    if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new TokenExpiredException("Token đặt lại mật khẩu đã hết hạn");
    }

    // Cập nhật mật khẩu
    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    // Fix: Sử dụng LocalDateTime cho updatedAt
    user.setUpdatedAt(LocalDateTime.now());

    userRepository.save(user);

    // Xóa token đặt lại mật khẩu đã sử dụng
    verificationTokenRepository.delete(resetToken);

    // Vô hiệu hóa tất cả các token đăng nhập hiện có
    invalidateAllUserTokens(user);

    return PasswordResetResponse.builder()
            .success(true)
            .message("Đặt lại mật khẩu thành công")
            .build();
  }

  /**
   * Làm mới token
   */
  public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
    try {
      // Xác minh refresh token
      SignedJWT signedJWT = verifyToken(request.getRefreshToken(), true);

      // Lấy thông tin người dùng từ token
      String email = signedJWT.getJWTClaimsSet().getSubject();
      User user = userRepository.findByEmail(email)
              .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

      // Tạo token mới
      String newAccessToken = generateToken(user, false);

      return TokenRefreshResponse.builder()
              .accessToken(newAccessToken)
              .refreshToken(request.getRefreshToken())
              .tokenType("Bearer")
              .success(true)
              .build();

    } catch (JOSEException | ParseException e) {
      log.error("Lỗi khi làm mới token: {}", e.getMessage());
      throw new InvalidTokenException("Refresh token không hợp lệ hoặc đã hết hạn");
    }
  }

  /**
   * Kiểm tra token
   */
  public IntrospectResponse introspect(IntrospectRequest request) {
    boolean isValid = true;
    String scope = "";
    String subject = "";

    try {
      SignedJWT signedJWT = verifyToken(request.getToken(), false);
      scope = signedJWT.getJWTClaimsSet().getStringClaim("scope");
      subject = signedJWT.getJWTClaimsSet().getSubject();
    } catch (JOSEException | ParseException e) {
      isValid = false;
    }

    return IntrospectResponse.builder()
            .valid(isValid)
            .scope(isValid ? scope : null)
            .subject(isValid ? subject : null)
            .build();
  }

  /**
   * Đăng xuất
   */
  @Transactional
  public LogoutResponse logout(LogoutRequest request) {
    try {
      SignedJWT signedToken = verifyToken(request.getToken(), false);

      // Lấy thông tin token
      String jti = signedToken.getJWTClaimsSet().getJWTID();
      Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

      // Lưu token vào danh sách vô hiệu hóa
      InvalidatedToken invalidatedToken = InvalidatedToken.builder()
              .id(jti)
              .expiryDate(convertToLocalDateTime(expiryTime))
              .invalidatedAt(LocalDateTime.now())
              .build();

      invalidatedTokenRepository.save(invalidatedToken);

      // Nếu có refresh token, vô hiệu hóa cả refresh token
      if (request.getRefreshToken() != null && !request.getRefreshToken().isEmpty()) {
        try {
          SignedJWT refreshToken = verifyToken(request.getRefreshToken(), true);
          String refreshJti = refreshToken.getJWTClaimsSet().getJWTID();
          Date refreshExpiryTime = refreshToken.getJWTClaimsSet().getExpirationTime();

          InvalidatedToken invalidatedRefreshToken = InvalidatedToken.builder()
                  .id(refreshJti)
                  .expiryDate(convertToLocalDateTime(refreshExpiryTime))
                  .invalidatedAt(LocalDateTime.now())
                  .build();

          invalidatedTokenRepository.save(invalidatedRefreshToken);
        } catch (Exception e) {
          log.warn("Không thể vô hiệu hóa refresh token: {}", e.getMessage());
        }
      }

      return LogoutResponse.builder()
              .success(true)
              .message("Đăng xuất thành công")
              .build();

    } catch (JOSEException | ParseException e) {
      log.error("Lỗi khi đăng xuất: {}", e.getMessage());
      throw new InvalidTokenException("Token không hợp lệ");
    }
  }

  /**
   * Đăng nhập chi tiết
   */
  public JwtAuthenticationResponse login(AuthenticationRequest request, BindingResult bindingResult) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với email: " + request.getEmail()));

    // Kiểm tra mật khẩu
    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
    if (!authenticated) {
      throw new NotFoundException("Email hoặc mật khẩu không chính xác");
    }

    // Bỏ qua kiểm tra xác minh email cho admin
    if (!user.isVerified() && !isAdminUser(user)) {
      throw new NotFoundException("Tài khoản chưa được xác minh. Vui lòng kiểm tra email của bạn.");
    }

    // Tạo DTO user
    UserResponse userResponse = userMapper.toDTO(user);
    Set<RoleResponse> roles = user.getRoles()
            .stream()
            .map(role -> new RoleResponse(role.getName(), role.getDescription()))
            .collect(Collectors.toSet());
    userResponse.setRoles(roles);

    // Tạo token truy cập và token làm mới
    String accessToken = generateToken(user, false);
    String refreshToken = generateToken(user, true);

    return JwtAuthenticationResponse.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .user(userResponse)
            .expiresIn(accessTokenExpirySeconds)
            .tokenType("Bearer")
            .build();
  }
  private boolean isAdminUser(User user) {
    return user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleManager.ADMIN.name()));
  }

  /**
   * Tạo JWT token
   */
  private String generateToken(User user, boolean isRefreshToken) {
    long expiry = isRefreshToken ? refreshTokenExpirySeconds : accessTokenExpirySeconds;

    JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS512)
            .type(JOSEObjectType.JWT)
            .build();

    JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
            .subject(user.getEmail())
            .issuer("movieapp.com")
            .issueTime(new Date())
            .expirationTime(new Date(Instant.now().plus(expiry, ChronoUnit.SECONDS).toEpochMilli()))
            .jwtID(UUID.randomUUID().toString())
            .claim("userId", user.getId())
            .claim("name", user.getFullName());

    // Chỉ thêm scope cho access token
    if (!isRefreshToken) {
      claimsBuilder.claim("scope", buildScope(user));
    }

    // Thêm flag để phân biệt loại token
    claimsBuilder.claim("tokenType", isRefreshToken ? "refresh" : "access");

    Payload payload = new Payload(claimsBuilder.build().toJSONObject());
    JWSObject jwsObject = new JWSObject(jwsHeader, payload);

    try {
      jwsObject.sign(new MACSigner(signerKey));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Không thể tạo token", e);
      throw new RuntimeException("Lỗi khi tạo token: " + e.getMessage());
    }
  }

  /**
   * Xác minh token
   */
  private SignedJWT verifyToken(String token, boolean isRefreshToken) throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
    SignedJWT signedJWT = SignedJWT.parse(token);

    // Kiểm tra chữ ký
    boolean verified = signedJWT.verify(verifier);
    if (!verified) {
      throw new JOSEException("Chữ ký token không hợp lệ");
    }

    // Kiểm tra thời hạn
    Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
    if (expiryTime.before(new Date())) {
      throw new JOSEException("Token đã hết hạn");
    }

    // Kiểm tra loại token
    String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("tokenType");
    if (isRefreshToken && !"refresh".equals(tokenType)) {
      throw new JOSEException("Token không phải là refresh token");
    }

    if (!isRefreshToken && !"access".equals(tokenType)) {
      throw new JOSEException("Token không phải là access token");
    }

    // Kiểm tra token có bị vô hiệu hóa chưa
    String jti = signedJWT.getJWTClaimsSet().getJWTID();
    if (invalidatedTokenRepository.existsById(jti)) {
      throw new JOSEException("Token đã bị vô hiệu hóa");
    }

    return signedJWT;
  }

  /**
   * Dọn dẹp token hết hạn
   */
  @Transactional
  public void cleanupExpiredTokens() {
    // Xóa token vô hiệu hóa đã hết hạn
    LocalDateTime now = LocalDateTime.now();
    // Chuyển đổi LocalDateTime sang Date để phù hợp với phương thức repository
    Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

    int deleted = invalidatedTokenRepository.deleteByExpiryDateBefore(nowDate);

    // Xóa token xác minh đã hết hạn
    int verificationDeleted = verificationTokenRepository.deleteByExpiryDateBefore(nowDate);

    log.info("Đã xóa {} token vô hiệu hóa và {} token xác minh hết hạn", deleted, verificationDeleted);
  }

  /**
   * Vô hiệu hóa tất cả token của người dùng
   */
  @Transactional
  protected void invalidateAllUserTokens(User user) {
    // Thực hiện theo logic của bạn để vô hiệu hóa tất cả token
    // Trong triển khai thực tế, có thể cần lưu thông tin về token của người dùng
    log.info("Vô hiệu hóa tất cả token của người dùng: {}", user.getEmail());
  }

  /**
   * Xây dựng scope từ vai trò của người dùng
   */
  private String buildScope(User user) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    if (!CollectionUtils.isEmpty(user.getRoles())) {
      user.getRoles().forEach(role -> {
        stringJoiner.add("ROLE_" + role.getName());
        // Có thể thêm permissions nếu cần
      });
    }
    return stringJoiner.toString();
  }

  /**
   * Chuyển đổi Date sang LocalDateTime
   */
  private LocalDateTime convertToLocalDateTime(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}