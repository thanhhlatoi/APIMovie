package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.ReviewRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Entity.Review;
import com.example.Movie.API.Service.ReviewService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Object> createReview(@RequestBody ReviewRequest request) throws Exception {
        return ResponseBuilder.create().body(reviewService.createEntity(request)).status(HttpStatus.OK).build();
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable long id,@RequestBody ReviewRequest request){
        return ResponseBuilder.create().body(reviewService.updateEntity(id, request)).status(HttpStatus.OK).build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable long id){
        reviewService.deleteEntity(id);
        return ResponseBuilder.create().status(HttpStatus.OK).body("xoa thanh cong").build();
    }
    @GetMapping
    public ResponseEntity<Object> findAllReviews(Pagination pageable){
        return ResponseBuilder.create().body(reviewService.getAll(pageable)).status(HttpStatus.OK).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> findReviewById(@PathVariable long id){
        return ResponseBuilder.create().body(reviewService.getById(id)).status(HttpStatus.OK).build();
    }

}
