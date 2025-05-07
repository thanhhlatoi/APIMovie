package com.example.Movie.API.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Movie.API.Entity.MovieProduct;

@Repository
public interface MovieProductRepository extends JpaRepository<MovieProduct, Long> {
  @Query("SELECT mp FROM MovieProduct mp LEFT JOIN FETCH mp.movieVideo WHERE mp.id = :id")
  Optional<MovieProduct> findByIdWithMovieVideo(@Param("id") Long id);
  // Tìm kiếm phim theo tiêu đề
  @Query("SELECT m FROM MovieProduct m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
  List<MovieProduct> findByTitleContaining(String title);

  // Tìm kiếm phim theo năm phát hành
  List<MovieProduct> findByYear(String year);

  // Tìm kiếm phim theo thể loại
  @Query("SELECT m FROM MovieProduct m JOIN m.genres g WHERE g.id = :genreId")
  List<MovieProduct> findByGenreId(@Param("genreId") Long genreId);

  // Tìm kiếm phim theo danh mục
  List<MovieProduct> findByCategoryId(Long categoryId);

  // Tìm kiếm phim theo tác giả
  List<MovieProduct> findByAuthorId(Long authorId);

  // Tìm kiếm phim theo diễn viên
  @Query("SELECT m FROM MovieProduct m JOIN m.performers p WHERE p.id = :performerId")
  List<MovieProduct> findByPerformerId(@Param("performerId") Long performerId);

}
