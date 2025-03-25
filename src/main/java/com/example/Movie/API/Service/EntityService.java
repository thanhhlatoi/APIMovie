package com.example.Movie.API.Service;

import com.example.Movie.API.DTO.Response.AuthorResponse;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EntityService<R,D> {
  D createEntity(R request) throws Exception;
  D updateEntity(long id, R entity);
  void deleteEntity(long id);
//  List<D> getAll();
  Page<D> getAll(Pagination pagination);
  D getById(long id);

}
// R == Request
// D == Response