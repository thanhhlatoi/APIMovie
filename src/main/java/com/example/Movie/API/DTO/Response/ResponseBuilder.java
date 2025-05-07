package com.example.Movie.API.DTO.Response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder<T> {
  private HttpStatus status;
  private T body;
  private T error;
  private HttpHeaders headers;
  private T contentType;

  public static <T> ResponseBuilder<T> create() {
    return new ResponseBuilder<>();
  }

  public ResponseBuilder<T> body(T body) {
    this.body = body;
    return this;
  }

  public ResponseBuilder<T> error(T error) {
    this.error = error;
    return this;
  }

  public ResponseBuilder<T> status(HttpStatus status) {
    this.status = status;
    return this;
  }

  public ResponseBuilder<T> header(HttpHeaders header) {
    this.headers = header;
    return this;
  }

  public ResponseBuilder<T> contentType(MediaType... contentType) {
    for (MediaType mediaType : contentType) {
      this.headers.setContentType(mediaType);
    }
    return this;
  }

  public ResponseEntity<Object> build() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", this.status.value());
    if (this.error != null) {
      response.put("error", this.error);
    }
    if (this.headers != null) {
      return new  ResponseEntity<>(this.body, headers, status);
    }
    if (this.body != null) {
      response.put("data", this.body);
    }

    return new ResponseEntity<>(response, status);
  }
}
