package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Service.Impl.MinioServiceImpl;
import com.example.Movie.API.Service.MinioService;
import com.example.Movie.API.Service.MovieProductService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/api/movieProduct")
public class MovieProductController {
  @Autowired
  private MovieProductService movieProductService;
  @Autowired
  private MinioServiceImpl minioService;

  @PostMapping
  public ResponseEntity<Object> createMovie(@ModelAttribute MovieProductRequest request) throws Exception {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieProductService.createEntity(request)).build();
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateMovie(@PathVariable long id, @ModelAttribute MovieProductRequest request) {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieProductService.updateEntity(id, request)).build();
  }

  @DeleteMapping("delete/{id}")
  public ResponseEntity<Object> deleteMovie(@PathVariable long id) {
    movieProductService.deleteEntity(id);
    return ResponseBuilder.create().status(HttpStatus.OK).body("delete thanh cong").build();
  }


    // like video
    @PutMapping("/{id}/like")
    public ResponseEntity<Object> likeMovie(@PathVariable Long id) {
      return ResponseBuilder.create().body(movieProductService.likeMovie(id)).status(HttpStatus.OK).build();
    }
    //disklike
  @PutMapping("/{id}/disklike")
  public ResponseEntity<Object> dislikeMovie(@PathVariable Long id) {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieProductService.dislikesMovie(id)).build();
  }
  //get anh
  @GetMapping("/view")
  public ResponseEntity<byte[]> viewFile(
          @RequestParam String bucketName,
          @RequestParam String path) throws IOException {

    byte[] fileData = minioService.getObject(bucketName, path);

    // Xác định content type dựa trên đuôi file
    String contentType = Files.probeContentType(Paths.get(path));
    if (contentType == null) {
      contentType = "application/octet-stream"; // fallback nếu không xác định được
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
  }
  ///
  @GetMapping("/search")
  public ResponseEntity<Object> searchMovies(@RequestParam("title") String title) {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieProductService.searchMoviesByTitle(title)).build();
  }
  @GetMapping("/{id}/with-video")
  public ResponseEntity<Object> getMovieWithVideo(@PathVariable Long id) {
    return ResponseBuilder.create().status(HttpStatus.OK).body(movieProductService.getMovieProductWithVideo(id)).build();
  }
  @GetMapping
  public ResponseEntity<Object> getAllMovies(Pagination pageable) {
    return ResponseBuilder.create().body(movieProductService.getAll(pageable)).status(HttpStatus.OK).build();
  }
  @GetMapping("/{id}")
  public ResponseEntity<Object> getMovieById(@PathVariable long id) {
    return ResponseBuilder.create().body(movieProductService.getById(id)).status(HttpStatus.OK).build();
  }
  // Tìm kiếm cơ bản - chỉ dùng một tiêu chí
  @GetMapping("/search/basic")
  public ResponseEntity<List<MovieProduct>> searchMoviesBasic(
          @RequestParam(required = false) String title,
          @RequestParam(required = false) String year,
          @RequestParam(required = false) Long genreId,
          @RequestParam(required = false) Long categoryId,
          @RequestParam(required = false) Long authorId,
          @RequestParam(required = false) Long performerId) {

    List<MovieProduct> results;

    if (title != null && !title.isEmpty()) {
      results = movieProductService.searchMoviesByTitle(title);
    } else if (year != null && !year.isEmpty()) {
      results = movieProductService.searchMoviesByYear(year);
    } else if (genreId != null) {
      results = movieProductService.searchMoviesByGenre(genreId);
    } else if (categoryId != null) {
      results = movieProductService.searchMoviesByCategory(categoryId);
    } else if (authorId != null) {
      results = movieProductService.searchMoviesByAuthor(authorId);
    } else if (performerId != null) {
      results = movieProductService.searchMoviesByPerformer(performerId);
    } else {
      // Nếu không có tham số nào, trả về danh sách rỗng
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(results);
  }

}
