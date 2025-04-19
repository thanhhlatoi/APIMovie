package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.GenreRequest;
import com.example.Movie.API.DTO.Response.GenreResponse;
import com.example.Movie.API.Entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GenreMapper extends EntityMapper<GenreRequest, GenreResponse, Genre>{

}
