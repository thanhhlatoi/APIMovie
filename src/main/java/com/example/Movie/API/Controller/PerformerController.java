package com.example.Movie.API.Controller;

import com.example.Movie.API.DTO.Request.PerformerRequest;
import com.example.Movie.API.DTO.Response.ResponseBuilder;
import com.example.Movie.API.Service.PerformerService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/performer")
public class PerformerController {

    @Autowired
    private PerformerService performerService;
    @PostMapping
    public ResponseEntity<Object> addPerformer(@ModelAttribute PerformerRequest request) throws Exception {
        return ResponseBuilder.create().body(performerService.createEntity(request)).status(HttpStatus.OK).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updatePerformer(@PathVariable long id, @ModelAttribute PerformerRequest request) {
        return ResponseBuilder.create().body(performerService.updateEntity(id, request)).status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePerformer(@PathVariable long id) {
        performerService.deleteEntity(id);
        return ResponseBuilder.create().body("xoa thanh cong").status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<Object> getAllPerformer(Pagination pageable) {
        return ResponseBuilder.create().body(performerService.getAll(pageable)).status(HttpStatus.OK).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPerformerById(@PathVariable long id) {
        return ResponseBuilder.create().body(performerService.getById(id)).status(HttpStatus.OK).build();
    }
}
