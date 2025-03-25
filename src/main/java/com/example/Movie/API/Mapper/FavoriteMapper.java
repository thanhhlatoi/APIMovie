package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.FavoriteRequest;
import com.example.Movie.API.DTO.Response.FavoriteResponse;
import com.example.Movie.API.Entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FavoriteMapper extends EntityMapper<FavoriteRequest, FavoriteResponse, Favorite>{
}
