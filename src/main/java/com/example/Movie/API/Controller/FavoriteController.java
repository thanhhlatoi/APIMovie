package com.example.Movie.API.Controller;


import com.example.Movie.API.DTO.Request.FavoriteRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.FavoriteService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<Object> createFavorite(@RequestBody FavoriteRequest request) throws Exception {
        return ResponseBuilder.create().status(HttpStatus.OK).body(favoriteService.createEntity(request)).build();
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable long id) {
        favoriteService.deleteEntity(id);
        return ResponseBuilder.create().status(HttpStatus.OK).body("delete thanh cong").build();
    }
    @GetMapping
    public ResponseEntity<Object> getAllMovies(Pagination pageable) {
        return ResponseBuilder.create().body(favoriteService.getAll(pageable)).status(HttpStatus.OK).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMovieById(@PathVariable long id) {
        return ResponseBuilder.create().body(favoriteService.getById(id)).status(HttpStatus.OK).build();
    }
}
