package com.example.demo.controller;

import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("user/shoppingCart")
@RestController
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addTypeProduct(@RequestBody ShoppingCartDto shoppingCartDto) {
        return shoppingCartService.addCart(shoppingCartDto);
    }
    @PostMapping("/payment")
    public ResponseEntity<?>  payment() {
        return shoppingCartService. payment();
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        return shoppingCartService.getAll();
    }

    @PostMapping("/getCartByUser")
    public ResponseEntity<?> getCartByUser() {
        return shoppingCartService.getCartByUser();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delteteByIdShoppingCartDetail(@RequestParam("id") long id) {
        return shoppingCartService.delteteByIdShoppingCartDetail(id);
    }


}
