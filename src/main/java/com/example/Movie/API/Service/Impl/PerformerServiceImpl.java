package com.example.Movie.API.Service.Impl;

import com.example.Movie.API.DTO.Request.PerformerRequest;
import com.example.Movie.API.DTO.Response.PerformerResponse;
import com.example.Movie.API.Entity.Author;
import com.example.Movie.API.Entity.Performer;
import com.example.Movie.API.Mapper.PerformerMapper;
import com.example.Movie.API.Repository.PerformerRepository;
import com.example.Movie.API.Service.PerformerService;
import com.example.Movie.API.Utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PerformerServiceImpl implements PerformerService {
    @Autowired
    private PerformerRepository performerRepository;
    @Autowired
    private PerformerMapper performerMapper;
    @Autowired
    private MinioServiceImpl minioService;
    @Override
    public PerformerResponse createEntity(PerformerRequest request) throws Exception {
        var performer = performerMapper.requestToEntity(request);
        final String fileStr = "Performer/" + request.getFileAvatar().getOriginalFilename();
        minioService.upLoadFile(request.getFileAvatar(), fileStr);
        performer.setAvatar(fileStr);
        performerRepository.save(performer);
        return performerMapper.toDTO(performer);
    }

    @Override
    public PerformerResponse updateEntity(long id, PerformerRequest entity) {
        var performer = performerRepository.findById(id).orElse(null);
        final String fileStr = "Performer/" + entity.getFileAvatar().getOriginalFilename();
        minioService.upLoadFile(entity.getFileAvatar(), fileStr);
        performer.setAvatar(fileStr);
        performerMapper.updateEntity(entity, performer);
        performerRepository.save(performer);
        return performerMapper.toDTO(performer);
    }

    @Override
    public void deleteEntity(long id) {

    }

    @Override
    public Page<PerformerResponse> getAll(Pagination pagination) {
        Page<Performer> performer = performerRepository.findAll(pagination);
        return performer.map(performerMapper::toDTO);
    }



    @Override
    public PerformerResponse getById(long id) {
        return null;
    }
}
