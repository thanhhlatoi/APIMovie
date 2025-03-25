package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.CategoryRequest;
import com.example.Movie.API.DTO.Response.CategoryResponse;
import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Entity.Category;
import com.example.Movie.API.Mapper.CategoryMapper;
import com.example.Movie.API.Repository.CategoryRepository;
import com.example.Movie.API.Service.CategoryService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private CategoryMapper categoryMapper;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public CategoryResponse createEntity(CategoryRequest request) {
    Category category = categoryMapper.requestToEntity(request);
    category.setActive(true);
    categoryRepository.save(category);
    return categoryMapper.toDTO(category);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public CategoryResponse updateEntity(long id, CategoryRequest entity) {
    Category category = categoryRepository.findById(id).orElseThrow();
    categoryMapper.updateEntity(entity,category);
    return categoryMapper.toDTO(category);
  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void deleteEntity(long id) {
    var category = categoryRepository.findById(id).orElseThrow();
    categoryRepository.delete(category);
  }

  @Override
  public Page<CategoryResponse> getAll(Pagination pagination) {
    Page<Category> categories = categoryRepository.findAll(pagination);
    return categories.map(categoryMapper::toDTO);
  }

//  @Override
//  public List<CategoryResponse> getAll() {
//    List<Category> users = categoryRepository.findAll();
//    return users.stream().map(user -> {
//      return categoryMapper.toDTO(user);
//    }).collect(Collectors.toList());
//  }
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public CategoryResponse getById(long id) {
    var users = categoryRepository.findById(id).orElse(null);
    return users == null ? null : categoryMapper.toDTO(users);
  }
}
