package com.example.demo.controller;

import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update-Cart")
    public ResponseEntity<?> updateCart(@RequestParam("idCartDetail") long idCartDetail, @RequestParam("quantityCart") int quantityCart) {
        return shoppingCartService.updateCart(idCartDetail, quantityCart);
<<<<<<< HEAD
=======
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment() {
        return shoppingCartService.paymentCart();
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        return shoppingCartService.getAllCard();
>>>>>>> fbd9aa51f0af72ec3d5f152ef13d047e123434b0
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
        return shoppingCartService.getCartByUser();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByIdShoppingCartDetail(@RequestParam("id") long id) {
        return shoppingCartService.deleteByIdShoppingCartDetail(id);
    }


}
