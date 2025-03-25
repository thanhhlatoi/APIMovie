package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.AuthorRequest;
import com.example.Movie.API.DTO.Response.AuthorResponse;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.AuthorService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {
  @Autowired
  private AuthorService authorService;
  @PostMapping
  public ResponseEntity<Object> addAuthor(@RequestBody AuthorRequest request) throws Exception {
    return ResponseBuilder.create().body(authorService.createEntity(request)).status(HttpStatus.OK).build();
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateAuthor(@PathVariable long id, @RequestBody AuthorRequest request) {
    return ResponseBuilder.create().body(authorService.updateEntity(id, request)).status(HttpStatus.OK).build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Object> deleteAuthor(@PathVariable long id) {
    authorService.deleteEntity(id);
    return ResponseBuilder.create().body("xoa thanh cong").status(HttpStatus.OK).build();
  }

  @GetMapping
  public ResponseEntity<Object> getAllAuthor(Pagination pageable) {
    Page<AuthorResponse> authorPage = authorService.getAll(pageable);
    return ResponseBuilder.create().body(authorPage).status(HttpStatus.OK).build();
  }
  @GetMapping("/{id}")
  public ResponseEntity<Object> getAuthorById(@PathVariable long id) {
    return ResponseBuilder.create().body(authorService.getById(id)).status(HttpStatus.OK).build();
  }
}
