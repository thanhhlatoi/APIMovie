package com.example.Movie.API.Exception;

public class VideoUploadException extends RuntimeException {
  public VideoUploadException(String message) {
    super(message);
  }

  public VideoUploadException(String message, Throwable cause) {
    super(message, cause);
  }
}
