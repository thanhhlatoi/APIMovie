package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.CategoryRequest;
import com.example.Movie.API.DTO.Response.CategoryResponse;
import com.example.Movie.API.Entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper extends EntityMapper<CategoryRequest, CategoryResponse, Category> {
}
