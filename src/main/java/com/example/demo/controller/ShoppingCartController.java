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

    @PutMapping("/update-Cart")
    public ResponseEntity<?> updateCart(@RequestParam("idCartDetail") long idCartDetail, @RequestParam("quantityCart") int quantityCart) {
        return shoppingCartService.updateCart(idCartDetail, quantityCart);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment() {
        return shoppingCartService.paymentCart();
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        return shoppingCartService.getAllCard();
    }

    @PostMapping("/getCartByUser")
    public ResponseEntity<?> getCartByUser() {
        return shoppingCartService.getCartByUser();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByIdShoppingCartDetail(@RequestParam("id") long id) {
        return shoppingCartService.deleteByIdShoppingCartDetail(id);
    }


}
