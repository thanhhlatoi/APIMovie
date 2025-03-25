package com.example.Movie.API.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PerformerRequest {
    private String fullName;
    private Date birthday;
    private boolean gender;
    private String country;
    private String describe;
}
