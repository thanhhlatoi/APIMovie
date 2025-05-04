package com.example.Movie.API.Exception;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException(String message) {
    super(message);
  }
}
