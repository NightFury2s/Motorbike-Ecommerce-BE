package com.example.demo.service;


import com.example.demo.model.Dto.ReviewsDto;
import com.example.demo.model.entity.Reviews;
import org.springframework.http.ResponseEntity;

public interface ReviewsService {
    ResponseEntity<?> add(ReviewsDto reviewsDto);
    ResponseEntity<?> get(Long productID);

    ResponseEntity<?> delete(long id);
}
