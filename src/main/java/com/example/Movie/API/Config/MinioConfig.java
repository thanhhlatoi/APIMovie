package com.example.Movie.API.Config;


import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@Getter
public class MinioConfig {
  @Value("${spring.minio.endpoint}")
  private String endpoint;

  @Value("${spring.minio.access-key}")
  private String accessKey;

  @Value("${spring.minio.secret-key}")
  private String secretKey;

  @Value("${spring.minio.bucket-name}")
  private String bucketName;

  @Bean
  public MinioClient minioClient(){
    return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
  }


}
