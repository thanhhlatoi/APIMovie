package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.MovieProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MovieProductMapper extends EntityMapper<MovieProductRequest, MovieProductResponse, MovieProduct> {

  // Cập nhật thông tin từ Request vào Entity
  @Override
  @Mapping(target = "genres", ignore = true)
  @Mapping(target = "performers", ignore = true)
  void updateEntity(MovieProductRequest request, @MappingTarget MovieProduct movieProduct);

  @Override
  @Mapping(target = "genres", ignore = true)
  @Mapping(target = "performers", ignore = true)
  MovieProduct requestToEntity(MovieProductRequest request);

  @Override
//  @Mapping(target = "genres", ignore = true)
//  @Mapping(target = "performers", ignore = true)
  MovieProductResponse toDTO(MovieProduct entity);
}

