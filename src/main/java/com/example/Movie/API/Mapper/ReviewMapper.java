package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.ReviewRequest;
import com.example.Movie.API.DTO.Response.ReviewResponse;
import com.example.Movie.API.Entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper extends EntityMapper<ReviewRequest, ReviewResponse, Review> {
}
