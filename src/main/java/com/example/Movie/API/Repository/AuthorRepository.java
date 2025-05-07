package com.example.Movie.API.Repository;


import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Search.SearchAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
  // Tìm kiếm tác giả theo tên
  List<Author> findByFullNameContainingIgnoreCase(String fullName);

  // Tìm kiếm tác giả theo quốc gia
  List<Author> findByCountryIgnoreCase(String country);
}
