package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private User user;
    private MovieProduct movieProduct;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
