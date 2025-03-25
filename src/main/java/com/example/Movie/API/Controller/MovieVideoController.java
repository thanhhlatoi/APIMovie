package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.MovieVideoRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.Impl.HlsServiceImpl;
import com.example.Movie.API.Service.MovieVideoService;
import com.example.Movie.API.Utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/movievideo")
@Slf4j
public class MovieVideoController {
  @Autowired
  private MovieVideoService movieVideoService;
  @Autowired
  private HlsServiceImpl hlsService;
  @PostMapping
  private ResponseEntity<Object> createMovieVideo(@ModelAttribute MovieVideoRequest movieVideoRequest) throws Exception {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieVideoService.createEntity(movieVideoRequest)).build();
  }
  @GetMapping
  private ResponseEntity<Object> getMovieVideo(Pagination pageable){
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieVideoService.getAll(pageable)).build();
  }
  @PostMapping("/upload")
  public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
    try {
      log.info("📤 Nhận video: {}", file.getOriginalFilename());

      // Gọi service để upload và chuyển đổi video
      String hlsPath = hlsService.uploadFile(file);
      if (hlsPath.isEmpty()) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi khi xử lý video!");
      }

      return ResponseEntity.ok().body("✅ Video HLS: " + hlsPath);
    } catch (Exception e) {
      log.error("❌ Lỗi khi upload video: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi upload video!");
    }
  }
}
