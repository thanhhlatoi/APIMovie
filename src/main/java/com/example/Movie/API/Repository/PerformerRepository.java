package com.example.Movie.API.Repository;

import com.example.Movie.API.Entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformerRepository extends JpaRepository<Performer, Long> {
  // Tìm kiếm diễn viên theo tên
  List<Performer> findByFullNameContainingIgnoreCase(String fullName);

  // Tìm kiếm diễn viên theo quốc gia
  List<Performer> findByCountryIgnoreCase(String country);
}
