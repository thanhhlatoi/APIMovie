package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.AuthenticationRequest;
import com.example.Movie.API.DTO.Request.IntrospectRequest;
import com.example.Movie.API.DTO.Request.LogoutRequest;
import com.example.Movie.API.DTO.Response.AuthenticationResponse;
import com.example.Movie.API.DTO.Response.IntrospectResponse;
import com.example.Movie.API.Exception.NotFoundException;
import com.example.Movie.API.Service.Impl.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/token")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    try {
      var isAuthenticated = authenticationService.authenticate(request);
      return ResponseEntity.ok(isAuthenticated);
    } catch (NotFoundException ex) {
      // Nếu không tìm thấy user, trả về lỗi với false
      AuthenticationResponse response = new AuthenticationResponse();
      response.setAuthenticated(false);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
      // Nếu có lỗi khác, trả về lỗi với false
      AuthenticationResponse response = new AuthenticationResponse();
      response.setAuthenticated(false);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  // kiem tra token het han hay chua
  @PostMapping("/introspect")
  public ResponseEntity<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {

    var isAuthenticated = authenticationService.introspect(request);
    // Trả về đối tượng AuthenticationResponse trong ResponseEntity
    return ResponseEntity.ok(isAuthenticated);

  }

  @PostMapping("/logout")
  private ResponseEntity<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
    authenticationService.logout(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest signInRequest,
                                 BindingResult bindingResult) {
    return ResponseEntity.ok(authenticationService.login(signInRequest, bindingResult));
  }
}

