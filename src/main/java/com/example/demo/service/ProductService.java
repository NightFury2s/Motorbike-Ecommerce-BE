package com.example.demo.service;

import com.example.demo.model.Dto.ProductDto;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity<?> add(ProductDto dto);
    ResponseEntity<?> getAll(int page, int size);
    //lọc theo type
    ResponseEntity<?> getSome(int page, int size,Long idType);
    ResponseEntity<?> getTypeDetail(int page, int size,Long detailType);
    ResponseEntity<?> getDetail(Long idProduct);
    //rank
    ResponseEntity<?> getTypeDetailArrange(int page, int size,Long detailType,String arrange);

    ResponseEntity<?> getByTypeProduct_IdArrange(int page, int size,Long typeProduct_Id,String arrange);

    ResponseEntity<?> delete(long id);
    ResponseEntity<?> put(long id,ProductDto productDto);
}
