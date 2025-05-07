package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.GenreRequest;
import com.example.Movie.API.DTO.Response.GenreResponse;
import com.example.Movie.API.Entity.Genre;
import com.example.Movie.API.Mapper.GenreMapper;
import com.example.Movie.API.Repository.GenreRepository;
import com.example.Movie.API.Service.GenreService;
import com.example.Movie.API.Utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {
  @Autowired
  private GenreRepository genreRepository;
  @Autowired
  private GenreMapper categoryMapper;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public GenreResponse createEntity(GenreRequest request) {
    Genre genre = categoryMapper.requestToEntity(request);
    genre.setActive(true);
    genreRepository.save(genre);
    return categoryMapper.toDTO(genre);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public GenreResponse updateEntity(long id, GenreRequest entity) {
    Genre category = genreRepository.findById(id).orElseThrow();
    categoryMapper.updateEntity(entity,category);
    genreRepository.save(category);
    return categoryMapper.toDTO(category);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void deleteEntity(long id) {
    var category = genreRepository.findById(id).orElseThrow();
    genreRepository.delete(category);
  }

  @Override
  public Page<GenreResponse> getAll(Pagination pagination) {
    Page<Genre> categories = genreRepository.findAll(pagination);
    log.info("Category count: {}", categories.getContent().getFirst());
    return categories.map(categoryMapper::toDTO);
  }


  @Override
  public GenreResponse getById(long id) {
    var users = genreRepository.findById(id).orElse(null);
    return users == null ? null : categoryMapper.toDTO(users);
  }
}
