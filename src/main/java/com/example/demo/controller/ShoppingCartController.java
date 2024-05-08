package com.example.demo.controller;

import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("user/shopping-cart")
@RestController
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }


    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addTypeProduct(@RequestBody ShoppingCartDto shoppingCartDto) {
        return shoppingCartService.addCart(shoppingCartDto);
    }

    @PutMapping("/update-cart")
    public ResponseEntity<?> updateCart(@RequestParam("idCartDetail") long idCartDetail, @RequestParam("quantityCart") int quantityCart) {
        return shoppingCartService.updateCart(idCartDetail, quantityCart);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment() {
        return shoppingCartService.paymentCart();
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return shoppingCartService.getAllCard();
    }

    @PostMapping("/get-cart-by-user")
    public ResponseEntity<?> getCartByUser() {
        return shoppingCartService.getCartByUser(0);
    }

    @PostMapping("/get-cart-by-user-paid")
    public ResponseEntity<?> getCartByUserPaid() {
        return shoppingCartService.getCartByUser(1);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByIdShoppingCartDetail(@RequestParam("id") long id) {
        return shoppingCartService.deleteByIdShoppingCartDetail(id);
    }


}
