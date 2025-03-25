package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.PerformerRequest;
import com.example.Movie.API.DTO.Response.PerformerResponse;
import com.example.Movie.API.Entity.Performer;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PerformerMapper extends EntityMapper<PerformerRequest, PerformerResponse, Performer>{
}
