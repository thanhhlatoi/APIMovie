package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.MovieVideoRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.Impl.HlsServiceImpl;
import com.example.Movie.API.Service.MovieVideoService;
import com.example.Movie.API.Utils.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/movievideo")
@Slf4j
@RequiredArgsConstructor
public class MovieVideoController {
  private final MovieVideoService movieVideoService;
  private final HlsServiceImpl hlsService;

  @PostMapping
  public ResponseEntity<Object> createMovieVideo(@ModelAttribute MovieVideoRequest movieVideoRequest) {
    log.info("Creating movie video with productId: {}, filename: {}",
            movieVideoRequest.getMovieProductId(),
            movieVideoRequest.getFileVideo() != null ? movieVideoRequest.getFileVideo().getOriginalFilename() : "No file");

    try {
      return ResponseBuilder.create()
              .status(HttpStatus.CREATED)
              .body(movieVideoService.createEntity(movieVideoRequest))
              .build();
    } catch (Exception e) {
      log.error("Error creating movie video: {}", e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi tạo video: " + e.getMessage())
              .build();
    }
  }

  @GetMapping
  public ResponseEntity<Object> getMovieVideos(Pagination pageable) {
    log.info("Getting all movie videos with pagination: {}", pageable);
    try {
      return ResponseBuilder.create()
              .status(HttpStatus.OK)
              .body(movieVideoService.getAll(pageable))
              .build();
    } catch (Exception e) {
      log.error("Error getting movie videos: {}", e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi lấy danh sách video: " + e.getMessage())
              .build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getMovieVideoById(@PathVariable Long id) {
    log.info("Getting movie video with id: {}", id);
    try {
      return ResponseBuilder.create()
              .status(HttpStatus.OK)
              .body(movieVideoService.getById(id))
              .build();
    } catch (Exception e) {
      log.error("Error getting movie video with id {}: {}", id, e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi lấy thông tin video: " + e.getMessage())
              .build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateMovieVideo(@PathVariable Long id, @ModelAttribute MovieVideoRequest movieVideoRequest) {
    log.info("Updating movie video with id: {}", id);
    try {
      return ResponseBuilder.create()
              .status(HttpStatus.OK)
              .body(movieVideoService.updateEntity(id, movieVideoRequest))
              .build();
    } catch (Exception e) {
      log.error("Error updating movie video with id {}: {}", id, e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi cập nhật video: " + e.getMessage())
              .build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteMovieVideo(@PathVariable Long id) {
    log.info("Deleting movie video with id: {}", id);
    try {
      movieVideoService.deleteEntity(id);
      return ResponseBuilder.create()
              .status(HttpStatus.OK)
              .body("Xóa video thành công")
              .build();
    } catch (Exception e) {
      log.error("Error deleting movie video with id {}: {}", id, e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi xóa video: " + e.getMessage())
              .build();
    }
  }

  @PostMapping("/upload")
  public ResponseEntity<Object> uploadVideo(@RequestParam("file") MultipartFile file) {
    log.info("Uploading video: {}", file.getOriginalFilename());
    try {
      String hlsPath = hlsService.uploadFile(file);
      if (hlsPath.isEmpty() || hlsPath.startsWith("ERROR:")) {
        log.error("Error processing video: {}", hlsPath);
        return ResponseBuilder.create()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi xử lý video: " + hlsPath)
                .build();
      }

      return ResponseBuilder.create()
              .status(HttpStatus.OK)
              .body("Video HLS: " + hlsPath)
              .build();
    } catch (Exception e) {
      log.error("Error uploading video: {}", e.getMessage(), e);
      return ResponseBuilder.create()
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Lỗi khi upload video: " + e.getMessage())
              .build();
    }
  }
}