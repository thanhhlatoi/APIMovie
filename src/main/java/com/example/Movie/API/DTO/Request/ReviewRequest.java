package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {
    private long userId;
    private long movieProductId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
