package com.example.demo.service;

import com.example.demo.model.Dto.ShoppingCartDto;
import org.springframework.http.ResponseEntity;

public interface ShoppingCartService {
    ResponseEntity<?> payment();
    ResponseEntity<?> putCart(ShoppingCartDto shoppingCartDto);
    ResponseEntity<?> addCart(ShoppingCartDto shoppingCartDto);
    ResponseEntity<?> getAll();
    ResponseEntity<?> delteteByIdShoppingCartDetail(Long id);
    ResponseEntity<?> getCartByUser();

}
