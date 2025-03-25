package com.example.Movie.API.Mapper;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.Entity.MovieProduct;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

public interface EntityMapper<R,D,E>{
  //create
  E requestToEntity(R repDTO);
  //response
  D toDTO(E entity);
  //update
  void updateEntity(R repDTO, @MappingTarget E entity);

}