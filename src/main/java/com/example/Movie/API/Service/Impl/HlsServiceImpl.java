package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.Exception.VideoProcessingException;
import com.example.Movie.API.Exception.VideoUploadException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HlsServiceImpl {
  @Value("${spring.video.output-dir}")
  private String baseOutputDir;

  @Value("${spring.ffmpeg.path}")
  private String ffmpegPath;

  @Value("${spring.minio.bucket-name}")
  private String bucketName;

  @Value("${spring.video.max-upload-size:10737418240}") // 10GB
  private long maxUploadSize;

  @Value("${spring.video.supported-formats:mp4,avi,mov,mkv}")
  private Set<String> supportedFormats;

  @Value("${spring.hls.segment-duration:10}")
  private int segmentDuration;

  private final MinioClient minioClient;
  private final ExecutorService uploadExecutor;

  public HlsServiceImpl(MinioClient minioClient) {
    this.minioClient = minioClient;
    this.uploadExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
              Thread thread = Executors.defaultThreadFactory().newThread(r);
              thread.setDaemon(true);
              return thread;
            }
    );
  }

  /**
   * Upload video file and convert to HLS
   */
  public String uploadFile(MultipartFile file) {
    Path videoDir = null;
    try {
      // Validate file
      validateFile(file);

      // Create temp directory and save file
      String originalFileName = file.getOriginalFilename();
      String fileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
      videoDir = Paths.get(baseOutputDir, fileNameWithoutExt);
      Path savedFilePath = saveFile(file, videoDir);

      // Convert to HLS
      String hlsOutputPath = convertMp4ToHls(savedFilePath.toString(), videoDir.toString());

      // Upload to MinIO
      uploadFolderToMinIO(videoDir.toFile(), fileNameWithoutExt);

      // Cleanup temp files
      cleanupTempFiles(videoDir);

      return hlsOutputPath;

    } catch (IllegalArgumentException e) {
      log.warn("Upload validation error: {}", e.getMessage());
      return "ERROR: " + e.getMessage();
    } catch (IOException e) {
      log.error("File handling error", e);
      return "ERROR: Không thể xử lý file: " + e.getMessage();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Process interrupted", e);
      return "ERROR: Quá trình xử lý bị gián đoạn";
    } catch (Exception e) {
      log.error("Unexpected error during file upload", e);
      return "ERROR: Lỗi không xác định: " + e.getMessage();
    } finally {
      // Ensure cleanup happens even if errors occur
      if (videoDir != null) {
        cleanupTempFiles(videoDir);
      }
    }
  }

  /**
   * Validate uploaded file
   */
  private void validateFile(MultipartFile file) {
    // Validate filename
    String originalFileName = Optional.ofNullable(file.getOriginalFilename())
            .orElseThrow(() -> new IllegalArgumentException("Tên file không hợp lệ"));

    // Validate file size
    if (file.getSize() > maxUploadSize) {
      throw new IllegalArgumentException(
              String.format("Kích thước file vượt quá giới hạn %d MB", maxUploadSize / 1024 / 1024)
      );
    }

    // Validate file format
    if (!isSupportedFormat(originalFileName)) {
      throw new IllegalArgumentException(
              "Định dạng file không được hỗ trợ. Các định dạng hỗ trợ: " + supportedFormats
      );
    }
  }

  /**
   * Save file to temporary location
   */
  private Path saveFile(MultipartFile file, Path videoDir) throws IOException {
    Files.createDirectories(videoDir);
    Path savedFilePath = videoDir.resolve(file.getOriginalFilename());
    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, savedFilePath, StandardCopyOption.REPLACE_EXISTING);
    }
    log.info("Video saved: {}", savedFilePath);
    return savedFilePath;
  }

  /**
   * Convert video to HLS with detailed codec processing
   */
  /**
   * Convert video to HLS with multiple quality resolutions (280p, 560p, 780p, 1080p)
   */
  private String convertMp4ToHls(String inputPath, String outputDir) throws IOException, InterruptedException {
    Path inputFile = Paths.get(inputPath);
    if (!Files.exists(inputFile)) {
      throw new FileNotFoundException("File không tồn tại: " + inputPath);
    }

    // Ensure output directory exists
    Files.createDirectories(Paths.get(outputDir));

    // FFmpeg command with specified resolutions
    List<String> ffmpegCommands = new ArrayList<>(Arrays.asList(
            ffmpegPath,
            "-i", inputPath,
            // Split and scale filter for 4 different resolutions
            "-filter_complex",
            "[0:v]split=4[v1][v2][v3][v4];" +
                    "[v1]scale=280:-2,format=yuv420p[v1out];" +
                    "[v2]scale=560:-2,format=yuv420p[v2out];" +
                    "[v3]scale=780:-2,format=yuv420p[v3out];" +
                    "[v4]scale=1920:1080,format=yuv420p[v4out]",

            // 280p - lowest quality
            "-map", "[v1out]", "-map", "0:a",
            "-c:v:0", "libx264", "-b:v:0", "280k", "-maxrate:v:0", "280k", "-bufsize:v:0", "560k",

            // 560p - low quality
            "-map", "[v2out]", "-map", "0:a",
            "-c:v:1", "libx264", "-b:v:1", "560k", "-maxrate:v:1", "560k", "-bufsize:v:1", "1120k",

            // 780p - medium quality
            "-map", "[v3out]", "-map", "0:a",
            "-c:v:2", "libx264", "-b:v:2", "780k", "-maxrate:v:2", "780k", "-bufsize:v:2", "1560k",

            // 1080p - high quality
            "-map", "[v4out]", "-map", "0:a",
            "-c:v:3", "libx264", "-b:v:3", "1080k", "-maxrate:v:3", "1080k", "-bufsize:v:3", "2160k",

            // Audio configuration
            "-c:a", "aac", "-b:a", "128k", "-ac", "2",

            // HLS specific settings
            "-hls_time", String.valueOf(segmentDuration),
            "-hls_list_size", "0",
            "-hls_segment_filename", outputDir + "/output_%v_%05d.ts",
            "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3",
            "-master_pl_name", "master.m3u8",
            "-f", "hls",
            outputDir + "/index_%v.m3u8"
    ));

    // Execute FFmpeg with improved error handling
    ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommands)
            .redirectErrorStream(true);

    try {
      Process process = processBuilder.start();

      // Capture FFmpeg output asynchronously
      CompletableFuture<Void> logFuture = CompletableFuture.runAsync(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          reader.lines().forEach(line -> log.debug("FFmpeg output: {}", line));
        } catch (IOException e) {
          log.error("Error reading FFmpeg log", e);
        }
      });

      // Wait with timeout
      boolean finished = process.waitFor(2, TimeUnit.HOURS);
      if (!finished) {
        process.destroyForcibly();
        throw new VideoProcessingException("Chuyển đổi video vượt quá thời gian cho phép");
      }

      // Check exit code
      int exitCode = process.exitValue();
      if (exitCode != 0) {
        throw new VideoProcessingException("Chuyển đổi FFmpeg thất bại. Mã lỗi: " + exitCode);
      }

      log.info("HLS conversion successful: {}/master.m3u8", outputDir);
      return outputDir.replace("/tmp/videos/", "") + "/master.m3u8";

    } catch (IOException | InterruptedException e) {
      log.error("Video conversion error", e);
      throw e;
    }
  }

  /**
   * Upload HLS files to MinIO with parallel processing and retry mechanism
   */
  private void uploadFolderToMinIO(File folder, String minioFolder) {
    File[] files = Optional.ofNullable(folder.listFiles(f ->
            f.isFile() && (f.getName().endsWith(".ts") || f.getName().endsWith(".m3u8"))
    )).orElse(new File[0]);

    if (files.length == 0) {
      log.error("No files found for upload in: {}", folder.getAbsolutePath());
      return;
    }

    List<CompletableFuture<Void>> uploadTasks = Arrays.stream(files)
            .map(file -> CompletableFuture.runAsync(() -> uploadFileWithRetry(file, minioFolder), uploadExecutor))
            .collect(Collectors.toList());

    // Wait for all uploads to complete
    try {
      CompletableFuture.allOf(uploadTasks.toArray(new CompletableFuture[0])).join();
    } catch (CompletionException e) {
      throw new VideoUploadException("Lỗi khi tải lên tệp video", e.getCause());
    }
  }

  /**
   * Upload single file with retry mechanism
   */
  private void uploadFileWithRetry(File file, String minioFolder) {
    int maxRetries = 3;
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(minioFolder + "/" + file.getName())
                        .stream(inputStream, file.length(), -1)
                        .contentType(determineContentType(file))
                        .build()
        );
        log.info("Upload successful: {}", file.getName());
        return;
      } catch (Exception e) {
        if (attempt == maxRetries) {
          log.error("Upload failed after {} attempts: {} - {}",
                  maxRetries, file.getName(), e.getMessage());
          throw new VideoUploadException("Không thể tải lên file: " + file.getName(), e);
        }
        log.warn("Upload error, retrying attempt {}: {} - {}",
                attempt, file.getName(), e.getMessage());

        try {
          Thread.sleep(1000L * attempt);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  /**
   * Clean up temporary files after processing
   */
  private void cleanupTempFiles(Path videoDir) {
    try {
      if (Files.exists(videoDir)) {
        Files.walk(videoDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                  try {
                    Files.delete(path);
                    log.debug("Deleted: {}", path);
                  } catch (IOException e) {
                    log.warn("Failed to delete: {}", path, e);
                  }
                });
        log.info("Cleaned up temporary directory: {}", videoDir);
      }
    } catch (IOException e) {
      log.warn("Failed to cleanup temporary directory: {}", videoDir, e);
    }
  }

  /**
   * Get file extension
   */
  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1).toLowerCase();
  }

  /**
   * Check if file format is supported
   */
  private boolean isSupportedFormat(String fileName) {
    String fileExt = getFileExtension(fileName);
    return supportedFormats.stream()
            .anyMatch(format -> format.equalsIgnoreCase(fileExt));
  }

  /**
   * Determine content type of a file
   */
  private String determineContentType(File file) {
    try {
      return Optional.ofNullable(Files.probeContentType(file.toPath()))
              .orElse("application/octet-stream");
    } catch (IOException e) {
      log.warn("Cannot determine content type for {}", file.getName());
      return "application/octet-stream";
    }
  }

  /**
   * Update M3U8 file paths
   */
  public String updateM3U8File(String bucketName, String path) {
    try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(path)
            .build());
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

      String videoFolder = new File(path).getParentFile().getName();
      String baseUrl = "http://localhost:8082/api/videos/hls-stream?bucketName="
              + bucketName + "&path=" + videoFolder + "/";

      return reader.lines()
              .map(line -> line.endsWith(".ts") ? baseUrl + line : line)
              .collect(Collectors.joining("\n"));

    } catch (Exception e) {
      log.error("Error updating M3U8: {}", e.getMessage());
      throw new VideoProcessingException("Lỗi khi cập nhật file M3U8", e);
    }
  }

  /**
   * Gracefully shutdown resources
   */
  @PreDestroy
  public void shutdown() {
    log.info("Shutting down HLS service resources");
    uploadExecutor.shutdown();
    try {
      if (!uploadExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
        uploadExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      uploadExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}