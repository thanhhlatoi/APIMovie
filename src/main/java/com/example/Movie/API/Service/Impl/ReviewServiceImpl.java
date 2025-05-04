package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.ReviewRequest;
import com.example.Movie.API.DTO.Response.ReviewResponse;
import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Entity.MovieProduct;
import com.example.Movie.API.Entity.Review;
import com.example.Movie.API.Entity.User;
import com.example.Movie.API.Mapper.ReviewMapper;
import com.example.Movie.API.Repository.MovieProductRepository;
import com.example.Movie.API.Repository.ReviewRepository;
import com.example.Movie.API.Repository.UsersRepository;
import com.example.Movie.API.Service.ReviewService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MovieProductRepository movieProductRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createEntity(ReviewRequest request) {
        User user = usersRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
        MovieProduct movieProduct = movieProductRepository.findById(request.getMovieProductId()).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + request.getUserId());
        }
        if (movieProduct == null) {
            throw new RuntimeException("Movie product not found with ID: " + request.getMovieProductId());
        }

        Review review = reviewMapper.requestToEntity(request);
        review.setUser(user);
        review.setMovieProduct(movieProduct);

        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse updateEntity(long id, ReviewRequest entity) {
        Review review = reviewRepository.findById(id).orElse(null);
        reviewMapper.updateEntity(entity,review);
        assert review != null;
        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    @Override
    public void deleteEntity(long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        assert review != null;
        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewResponse> getAll(Pagination pagination) {
        Page<Review> review = reviewRepository.findAll(pagination);
        return review.map(reviewMapper::toDTO);
    }



    @Override
    public ReviewResponse getById(long id) {
        var review = reviewRepository.findById(id).orElse(null);
        return review == null ? null : reviewMapper.toDTO(review);
    }
}
