package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.UserUpdateRequest;
import com.example.Movie.API.DTO.Response.UserResponse;
import com.example.Movie.API.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserUpdateMapper extends EntityMapper<UserUpdateRequest, UserResponse, User>{
}
