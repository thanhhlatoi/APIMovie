package com.example.Movie.API.Service.Impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HlsServiceImpl {

  @Value("${spring.video.output-dir}")
  private String baseOutputDir;

  @Value("${spring.ffmpeg.path}")
  private String ffmpegPath;

  @Value("${spring.minio.bucket-name}")
  private String bucketName;

  private final MinioClient minioClient;

  public HlsServiceImpl(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  private String convertMp4ToHls(String inputPath, String outputDir) throws IOException, InterruptedException {
    File inputFile = new File(inputPath);
    if (!inputFile.exists()) {
      throw new IOException("❌ File không tồn tại: " + inputPath);
    }

    // Adaptive Bitrate (ABR) - Tạo nhiều độ phân giải
    String command = String.format(
            "%s -i %s " +
                    "-map 0:v -map 0:a " +
                    "-b:v:0 800k -s:v:0 640x360 " +
                    "-b:v:1 1200k -s:v:1 1280x720 " +
                    "-b:v:2 2500k -s:v:2 1920x1080 " +
                    "-c:v libx264 -preset fast -crf 23 -c:a aac -b:a 128k " +
                    "-hls_time 10 -hls_list_size 0 -f hls %s/output.m3u8",
            ffmpegPath, inputPath, outputDir
    );

    log.info("⚙️ Đang chạy FFmpeg: {}", command);

    // Chạy lệnh theo OS
    ProcessBuilder builder;
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      builder = new ProcessBuilder("cmd.exe", "/c", command);
    } else {
      builder = new ProcessBuilder("/bin/sh", "-c", command);
    }

    builder.redirectErrorStream(true);
    Process process = builder.start();

    // Đọc log từ FFmpeg
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info(line);
      }
    }

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IOException("❌ FFmpeg lỗi! Mã lỗi: " + exitCode);
    }

    log.info("✅ HLS tạo thành công: {}/output.m3u8", outputDir);
    return outputDir.replace("/tmp/videos/", "") + "/output.m3u8";
//    return outputDir + "/output.m3u8";
  }

  private void uploadFolderToMinIO(File folder, String minioFolder) {
    if (!folder.exists() || !folder.isDirectory()) {
      log.error("❌ Thư mục không tồn tại: {}", folder.getAbsolutePath());
      return;
    }

    File[] files = folder.listFiles();
    if (files == null) {
      log.error("❌ Không tìm thấy file nào trong thư mục: {}", folder.getAbsolutePath());
      return;
    }

    for (File file : files) {
      try (InputStream inputStream = new FileInputStream(file)) {
        log.info("📤 Upload file: {} lên MinIO...", file.getName());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(minioFolder + "/" + file.getName())
                        .stream(inputStream, file.length(), -1)
                        .contentType(Files.probeContentType(file.toPath()))
                        .build()
        );
        log.info("✅ Upload thành công: {}", file.getName());
      } catch (Exception e) {
        log.error("❌ Lỗi upload {}: {}", file.getName(), e.getMessage());
      }
    }
  }

public String updateM3U8File(String bucketName, String path) {
  try {
    // Lấy thư mục của file .m3u8
    String videoFolder = new File(path).getParentFile().getName();

    InputStream stream = minioClient.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(path)
            .build());

    List<String> lines = new BufferedReader(new InputStreamReader(stream))
            .lines()
            .collect(Collectors.toList());

    // Tạo base URL dựa trên videoFolder
    String baseUrl = "http://192.168.100.193:8082/api/videos/hls-stream?bucketName="
            + bucketName + "&path=" + videoFolder + "/";

    String updatedContent = lines.stream()
            .map(line -> line.endsWith(".ts") ? baseUrl + line : line)
            .collect(Collectors.joining("\n"));

    return updatedContent;
  } catch (Exception e) {
    log.error("Error updating M3U8 file: {}", e.getMessage());
    return null;
  }
}

  public String uploadFile(MultipartFile file) {
    try {
      // Kiểm tra và lấy tên file gốc
      String originalFileName = file.getOriginalFilename();
      if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".mp4")) {
        throw new IllegalArgumentException("❌ Chỉ hỗ trợ upload file .mp4");
      }

      // Lấy tên file không có phần mở rộng
      String fileNameWithoutExt = originalFileName.replace(".mp4", "");
      File videoDir = new File(baseOutputDir, fileNameWithoutExt);

      // Tạo thư mục chứa video nếu chưa tồn tại
      if (!videoDir.exists() && !videoDir.mkdirs()) {
        throw new IOException("❌ Không thể tạo thư mục: " + videoDir);
      }

      // Lưu file video vào thư mục
      File savedFile = new File(videoDir, originalFileName);
      file.transferTo(savedFile);
      log.info("✅ Video đã lưu: {}", savedFile.getAbsolutePath());

      // Chuyển đổi video sang HLS
      String hlsOutputPath = convertMp4ToHls(savedFile.getAbsolutePath(), videoDir.getAbsolutePath());

      // Upload thư mục chứa HLS lên MinIO
      uploadFolderToMinIO(videoDir, fileNameWithoutExt);

      // Trả về đường dẫn file M3U8 đã tạo
      return hlsOutputPath;
    } catch (IllegalArgumentException e) {
      log.warn("⚠️ Lỗi định dạng file: {}", e.getMessage());
      return "ERROR: " + e.getMessage();
    } catch (IOException e) {
      log.error("❌ Lỗi khi xử lý file: {}", e.getMessage(), e);
      return "ERROR: Không thể xử lý file.";
    } catch (Exception e) {
      log.error("❌ Lỗi không xác định khi upload file: {}", e.getMessage(), e);
      return "ERROR: Đã xảy ra lỗi.";
    }
  }


}


