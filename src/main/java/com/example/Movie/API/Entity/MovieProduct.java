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

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="movie_genre",joinColumns = @JoinColumn(name="movie_id"),inverseJoinColumns = @JoinColumn(name="genre_id"))
  private Set<Genre> genres = new HashSet<>();



  @OneToOne(mappedBy = "movieProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private MovieVideo movieVideo;

  @ManyToOne
  @JoinColumn(name = "authorId", nullable = false)
  @JsonIgnore
  private Author author;
  @ManyToOne
  @JoinColumn(name = "categoryId", nullable = true)
  @JsonIgnore
  private Category category;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="movie_performer",joinColumns = @JoinColumn(name="movie_id"),inverseJoinColumns = @JoinColumn(name="performer_id"))
  private Set<Performer> performers = new HashSet<>();
}
