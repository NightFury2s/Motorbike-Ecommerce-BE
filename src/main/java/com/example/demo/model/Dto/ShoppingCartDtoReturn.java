package com.example.demo.model.Dto;

import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ShoppingCart;
import com.example.demo.model.entity.ShoppingCartDetail;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ShoppingCartDtoReturn {

    private long idCart;
    private Date paymentDate;
    private int status;
    private double totalPrice;
    private List<ProductSomeReponseDto> shoppingCartDetailsDto = new ArrayList<>();

    public ShoppingCartDtoReturn(ShoppingCart shoppingCart) {
        this.idCart = shoppingCart.getId();
        this.status = shoppingCart.getStatus();
        this.paymentDate = shoppingCart.getPaymentDate();
        this.shoppingCartDetailsDto = shoppingCart.getShoppingCartDetails().stream()
                .map(detail -> new ProductSomeReponseDto(detail.getProduct()))
                .collect(Collectors.toList());
        this.totalPrice = shoppingCartDetailsDto.stream()
                .mapToDouble(ProductSomeReponseDto::getNewPrice)
                .sum();
    }
}
