package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.GenreRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.GenreService;
import com.example.Movie.API.Utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/genre")
public class GenreController {
  @Autowired
  private GenreService categoryService;

  @PostMapping
  public ResponseEntity<Object> addCategory(@RequestBody GenreRequest request) throws Exception {
    return ResponseBuilder.create().body(categoryService.createEntity(request)).status(HttpStatus.OK).build();
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateCategory(@PathVariable long id, @RequestBody GenreRequest request) {
    return ResponseBuilder.create().body(categoryService.updateEntity(id, request)).status(HttpStatus.OK).build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Object> deleteCategory(@PathVariable long id) {
    categoryService.deleteEntity(id);
    return ResponseBuilder.create().body("xoa thanh cong").status(HttpStatus.OK).build();
  }

  @GetMapping
  public ResponseEntity<Object> getAllCategory(Pagination pageable) {
    log.info("res: {}", ResponseBuilder.create().body(categoryService.getAll(pageable)).status(HttpStatus.OK).build());
    return ResponseBuilder.create().body(categoryService.getAll(pageable)).status(HttpStatus.OK).build();
  }
  @GetMapping("/{id}")
  public ResponseEntity<Object> getCategoryById(@PathVariable long id) {
    return ResponseBuilder.create().body(categoryService.getById(id)).status(HttpStatus.OK).build();
  }
}
