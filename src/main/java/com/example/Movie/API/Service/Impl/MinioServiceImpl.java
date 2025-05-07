package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.Config.MinioConfig;
import io.minio.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MinioServiceImpl {

  private final MinioClient minioClient;
  @Value("${spring.video.output-dir}")
  private String baseOutputDir;
//  private  String a = inputPath;

  @Value("${spring.ffmpeg.path}") // Thêm biến FFMPEG_PATH trong application.yml
  private String FFMPEG_PATH;

  @Value("${spring.minio.bucket-name}")
  private String bucketName;

  @Autowired
  private MinioConfig minioConfig;

  public MinioServiceImpl(@Value("${spring.minio.endpoint}") String endpoint,
                          @Value("${spring.minio.access-key}") String accessKey,
                          @Value("${spring.minio.secret-key}") String secretKey) {
    this.minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
  }

  // Upload file lên MinIO
  public String uploadVideo(MultipartFile file) throws Exception {
    String fileName = file.getOriginalFilename();
    try (InputStream inputStream = file.getInputStream()) {
      minioClient.putObject(
              PutObjectArgs.builder()
                      .bucket(bucketName)
                      .object(fileName)
                      .stream(inputStream, file.getSize(), -1)
                      .contentType(file.getContentType())
                      .build()
      );
    }
    return fileName;
  }
  //upload file anh
  public String upLoadFile(MultipartFile file, String path){
    try {
      boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!isExist) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }

      minioClient.putObject(
              PutObjectArgs.builder()
                      .bucket(bucketName)
                      .object(path)
                      .stream(file.getInputStream(), file.getSize(), -1)
                      .contentType(file.getContentType())
                      .build()
      );
      return path;
    } catch (Exception e) {
      log.error("error put from minio: {}", e.getMessage());
      return "";
    }
  }
//xem anh
  public byte[] getObject(String bucketName, String path) {
    try {
      // Lấy luồng dữ liệu từ MinIO
      InputStream inputStream = minioClient.getObject(
              GetObjectArgs.builder()
                      .bucket(bucketName)
                      .object(path)
                      .build()
      );
      return inputStream.readAllBytes(); // Trả về InputStreamResource
    } catch (Exception e) {
      log.error("Error while streaming file: {}", e.getMessage());
      return new byte[0];
    }
  }
  //Dat ten anh
  public String uploadFileImg(MultipartFile file, String directory) {
    String fileName = directory + "/" + file.getOriginalFilename();
    upLoadFile(file, fileName);
    return fileName;
  }

}
