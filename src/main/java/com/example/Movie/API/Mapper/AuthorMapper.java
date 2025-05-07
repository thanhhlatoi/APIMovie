package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.AuthorRequest;
import com.example.Movie.API.DTO.Response.AuthorResponse;
import com.example.Movie.API.Entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthorMapper extends EntityMapper<AuthorRequest, AuthorResponse, Author> {
}
