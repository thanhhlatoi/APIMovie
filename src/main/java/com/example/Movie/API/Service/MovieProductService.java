package com.example.Movie.API.Service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.Movie.API.DTO.Request.MovieProductRequest;
import com.example.Movie.API.DTO.Response.MovieProductResponse;
import com.example.Movie.API.Entity.MovieProduct;

@Repository
public interface MovieProductService extends EntityService<MovieProductRequest, MovieProductResponse> {
  MovieProduct likeMovie(long id );
  MovieProductResponse dislikesMovie(long id );
  List<MovieProduct> searchMoviesByTitle(String title);
  MovieProductResponse getMovieProductWithVideo(long id);
  // Tìm kiếm phim theo tiêu đề


  // Tìm kiếm phim theo năm
   List<MovieProduct> searchMoviesByYear(String year) ;

  // Tìm kiếm phim theo thể loại
   List<MovieProduct> searchMoviesByGenre(Long genreId) ;

  // Tìm kiếm phim theo danh mục
   List<MovieProduct> searchMoviesByCategory(Long categoryId) ;

  // Tìm kiếm phim theo tác giả
   List<MovieProduct> searchMoviesByAuthor(Long authorId) ;

  // Tìm kiếm phim theo diễn viên
   List<MovieProduct> searchMoviesByPerformer(Long performerId) ;
}
