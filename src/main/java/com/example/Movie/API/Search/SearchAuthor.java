package com.example.Movie.API.Search;

import com.example.Movie.API.Utils.Pagination;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchAuthor extends Pagination {
  private String fullName;
}
