package com.example.Movie.API.DTO.Response;

import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteResponse {
    private Long id;
    private User user;
    private MovieProduct movieProduct;

}
