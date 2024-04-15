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

@Getter
@Setter
public class ShoppingCartDtoReturn {
    private long idCart;
    private Date paymentDate;
 //   private long totalPriceCart;
    private int status;
    private List<ShoppingCartDetail> shoppingCartDetails = new ArrayList<>();

   public ShoppingCartDtoReturn(ShoppingCart shoppingCart){
        this.idCart=shoppingCart.getId();
        this.status=shoppingCart.getStatus();
        this.shoppingCartDetails = shoppingCart.getShoppingCartDetails();
        this.paymentDate=shoppingCart.getPaymentDate();
    }
}
