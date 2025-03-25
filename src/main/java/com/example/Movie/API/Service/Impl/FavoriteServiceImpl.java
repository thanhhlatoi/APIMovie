package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.FavoriteRequest;
import com.example.Movie.API.DTO.Response.FavoriteResponse;
import com.example.Movie.API.Entity.*;
import com.example.Movie.API.Mapper.FavoriteMapper;
import com.example.Movie.API.Repository.FavoriteRepository;
import com.example.Movie.API.Repository.MovieProductRepository;
import com.example.Movie.API.Repository.UsersRepository;
import com.example.Movie.API.Service.FavoriteService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MovieProductRepository movieProductRepository;

    @Override
    public FavoriteResponse createEntity(FavoriteRequest request) {
        User user = usersRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));;
        MovieProduct movieProduct = movieProductRepository.findById(request.getMovieProductId()).orElseThrow(() -> new RuntimeException("MovieProduct not found with ID: " + request.getMovieProductId()));;
        Favorite favorite = favoriteMapper.requestToEntity(request);
        favorite.setUser(user);
        favorite.setMovieProduct(movieProduct);
        favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(favorite);
    }

    @Override
    public FavoriteResponse updateEntity(long id, FavoriteRequest entity) {
        return null;
    }

    @Override
    public void deleteEntity(long id) {
        Favorite favorite = favoriteRepository.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found with ID: " ));;
        assert favorite != null;
        favoriteRepository.delete(favorite);
    }

    @Override
    public Page<FavoriteResponse> getAll(Pagination pagination) {
        Page<Favorite> favorites = favoriteRepository.findAll(pagination);
        return favorites.map(favoriteMapper::toDTO);
    }

//    @Override
//    public List<FavoriteResponse> getAll() {
//        List<Favorite> favorites = favoriteRepository.findAll();
//        return favorites.stream().map(user -> {
//            return favoriteMapper.toDTO(user);
//        }).collect(Collectors.toList());
//    }

    @Override
    public FavoriteResponse getById(long id) {
        var favorites = favoriteRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with ID: " ));;
        return favorites == null ? null : favoriteMapper.toDTO(favorites);
    }
}
