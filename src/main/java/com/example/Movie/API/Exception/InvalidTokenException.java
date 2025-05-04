package com.example.Movie.API.Exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException(String message) {
    super(message);
  }
}
