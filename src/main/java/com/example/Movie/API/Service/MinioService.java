package com.example.Movie.API.Service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
  String upLoadFile(MultipartFile file, String path);
  String viewUrlFile(String bucketName, String fileName);
  byte[] getObject(String bucketName, String path);
}
