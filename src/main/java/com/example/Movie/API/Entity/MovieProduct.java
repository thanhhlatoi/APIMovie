package com.example.Movie.API.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MovieProduct")
public class MovieProduct  extends AbstractEntity<Long> {
  private String title;
  private String description;
  private String time;
  private String year;
  private int likes;
  private int dislikes;
  private int views;
  private String imgMovie;
  @ManyToOne
  @JoinColumn(name = "genreId", nullable = false)
  private Genre genre;
  @ManyToOne
  @JoinColumn(name = "categoryId", nullable = false)
  private Category category;
  @OneToOne(mappedBy = "movieProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private MovieVideo movieVideo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinTable(name="movie_author",joinColumns = @JoinColumn(name="movie_id"),inverseJoinColumns = @JoinColumn(name="author_id"))
  private Author author;
  @ManyToMany
  @JoinTable(name="movie_performer",joinColumns = @JoinColumn(name="movie_id"),inverseJoinColumns = @JoinColumn(name="performer_id"))
  private Set<Performer> performer = new HashSet<>();
}
