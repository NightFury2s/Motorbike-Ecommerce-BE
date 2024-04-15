package com.example.demo.controller;

import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("user/shoppingCart")
@Controller
public class ShoppingCartController {
    // @Autowired
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addTypeProduct(@RequestBody ShoppingCartDto shoppingCartDto) {
        return shoppingCartService.addCart(shoppingCartDto);
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        return shoppingCartService.getAll();
    }

    @PostMapping
            ("/getCartByUser")
    public ResponseEntity<?> getCartByUser() {
        return shoppingCartService.getCartByUser();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delteteByIdShoppingCartDetail(@RequestParam("id") long id) {
        return shoppingCartService.delteteByIdShoppingCartDetail(id);
    }


}
