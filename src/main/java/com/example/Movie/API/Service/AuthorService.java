package com.example.Movie.API.Service;

import com.example.Movie.API.DTO.Request.AuthorRequest;
import com.example.Movie.API.DTO.Response.AuthorResponse;
import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Search.SearchAuthor;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.data.domain.Page;

public interface AuthorService extends EntityService<AuthorRequest,AuthorResponse>{

}
