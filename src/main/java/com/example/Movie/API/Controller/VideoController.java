package com.example.Movie.API.Controller;

import com.example.Movie.API.Service.Impl.HlsServiceImpl;
import com.example.Movie.API.Service.Impl.MinioServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
@Slf4j
@CrossOrigin(origins = "*") // Cho phép CORS
public class VideoController {

  @Autowired
  private MinioServiceImpl minioService;
  @Autowired
  private HlsServiceImpl hlsService;
  @Autowired
  private MinioClient minioClient;

  /**
   * Upload video và chuyển đổi sang HLS
   */
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

  /**
   * Stream file HLS (m3u8 hoặc segment .ts)
   */
  @GetMapping("/hls-stream")
  public ResponseEntity<?> streamHlsFile(
          @RequestParam String bucketName,
          @RequestParam String path) {
    log.info("📥 Yêu cầu stream HLS: bucket={}, path={}", bucketName, path);

    try {
      if (bucketName == null || path == null || path.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("❌ Thiếu thông tin bucket hoặc đường dẫn file.");
      }

      if (path.endsWith(".m3u8")) {
        String content = hlsService.updateM3U8File(bucketName, path);
        if (content == null) {
          log.warn("⚠️ File .m3u8 không tìm thấy: {}", path);
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File .m3u8 không tồn tại!");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(content);
      }

      return getHlsSegment(bucketName, path);
    } catch (Exception e) {
      log.error("❌ Lỗi khi stream HLS file: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server!");
    }
  }

  /**
   * Trả về từng phân đoạn HLS (.ts)
   */
  @GetMapping("/hls-segment")
  public ResponseEntity<Resource> getHlsSegment(
          @RequestParam String bucketName,
          @RequestParam String path) {
    log.info("📥 Tải phân đoạn HLS: bucket={}, path={}", bucketName, path);

    try {
      if (bucketName == null || path == null || path.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(null);
      }

      InputStream stream = minioClient.getObject(GetObjectArgs.builder()
              .bucket(bucketName)
              .object(path)
              .build());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.valueOf("video/mp2t"));

      return ResponseEntity.ok()
              .headers(headers)
              .body(new InputStreamResource(stream));
    } catch (Exception e) {
      log.warn("⚠️ Không tìm thấy phân đoạn: {}", path);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Xem ảnh từ MinIO
   */
  @GetMapping("/view")
  public ResponseEntity<byte[]> viewFile(
          @RequestParam String bucketName,
          @RequestParam String path) {
    try {
      log.info("📸 Lấy ảnh từ MinIO: bucket={}, path={}", bucketName, path);

      byte[] fileData = minioService.getObject(bucketName, path);
      if (fileData == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.IMAGE_JPEG); // Nếu là PNG thì đổi thành MediaType.IMAGE_PNG

      return ResponseEntity.ok()
              .headers(headers)
              .body(fileData);
    } catch (Exception e) {
      log.error("❌ Lỗi khi lấy ảnh: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
