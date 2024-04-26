package com.example.demo.service;

import com.example.demo.model.Dto.ProductDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<?> add(ProductDto dto);

    ResponseEntity<?> getAll(int page, int size);

    //lọc theo type
    ResponseEntity<?> getSome(int page, int size, Long idType);

    ResponseEntity<?> getTypeDetail(int page, int size, Long detailType);

    ResponseEntity<?> getDetail(Long idProduct);

    //rank
    ResponseEntity<?> getTypeDetailArrange(int page, int size, Long detailType, String arrange);

    ResponseEntity<?> getByTypeProduct_IdArrange(int page, int size, Long typeProduct_Id, String arrange);

    ResponseEntity<?> delete(long id);

    ResponseEntity<?> deleteMultipleProducts(List<Long> ids);

    ResponseEntity<?> put(long id, ProductDto productDto);
<<<<<<< HEAD

    public ResponseEntity<?> findByNameProduct(int page, int size, String nameProduct);

=======
>>>>>>> fbd9aa51f0af72ec3d5f152ef13d047e123434b0
}
