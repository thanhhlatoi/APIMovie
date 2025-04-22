package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.AuthorRequest;
import com.example.Movie.API.DTO.Response.AuthorResponse;
import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Mapper.AuthorMapper;
import com.example.Movie.API.Repository.AuthorRepository;
import com.example.Movie.API.Search.SearchAuthor;
import com.example.Movie.API.Service.AuthorService;
import com.example.Movie.API.Utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorServiceImpl implements AuthorService {
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private AuthorMapper authorMapper;
  @Autowired
  private MinioServiceImpl minioService;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public AuthorResponse createEntity(AuthorRequest request) throws Exception {
    Author author = authorMapper.requestToEntity(request);
    final String fileStr = "author/" + request.getFileAvatar().getOriginalFilename();
    minioService.upLoadFile(request.getFileAvatar(), fileStr);
    author.setAvatar(fileStr);
    authorRepository.save(author);
    return authorMapper.toDTO(author);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public AuthorResponse updateEntity(long id, AuthorRequest entity) {
    Author author = authorRepository.findById(id).orElse(null);
    final String fileStr = "author/" + entity.getFileAvatar().getOriginalFilename();
    minioService.upLoadFile(entity.getFileAvatar(), fileStr);
    assert author != null;
    author.setAvatar(fileStr);
    authorMapper.updateEntity(entity,author);
    authorRepository.save(author);
    return authorMapper.toDTO(author);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void deleteEntity(long id) {

  }

  @Override
  public Page<AuthorResponse> getAll(Pagination pagination) {
    Page<Author> author = authorRepository.findAll(pagination);
    return author.map(authorMapper::toDTO);
  }

  @PreAuthorize("hasRole('ADMIN')")



  @Override
  public AuthorResponse getById(long id) {
    Author author = authorRepository.findById(id).orElse(null);
    return author == null ? null : authorMapper.toDTO(author);
  }


}
