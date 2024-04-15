package com.example.demo.service.serviceImpl;

import ch.qos.logback.classic.pattern.Util;
import com.example.demo.Util.GetInfoUser;
import com.example.demo.model.Dto.Messenger;
import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.model.Dto.ShoppingCartDtoReturn;
import com.example.demo.model.entity.ShoppingCart;
import com.example.demo.model.entity.ShoppingCartDetail;
import com.example.demo.repositories.*;
import com.example.demo.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class ShoppingCartImpl implements ShoppingCartService {
    private final Messenger messenger;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartDetailRepository shoppingCartDetailRepository;

    public ShoppingCartImpl(
            Messenger messenger,
            ShoppingCartRepository shoppingCartRepository,
            UserRepository userRepository, ProductRepository productRepository, ShoppingCartDetailRepository shoppingCartDetailRepository

    ) {
        this.messenger = messenger;
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;

        this.productRepository = productRepository;

        this.shoppingCartDetailRepository = shoppingCartDetailRepository;
    }

    @Override
    public ResponseEntity<?> addCart(ShoppingCartDto shoppingCartDto) {
        try {
            // tạo 1 ShoppingCart để lưu vào db
            ShoppingCart shoppingCartNew = new ShoppingCart();

            //kiểm tra trong db đã tồn tại shoppingCart của user có trạng thái 0(chưa thanh toán) chưa
            if (shoppingCartRepository.existsByUser_UsernameAndStatus(GetInfoUser.getUsername(), 0)) {

                //lấy shoppingCartNew từ db
                shoppingCartNew = shoppingCartRepository.findByUsernameAndStatus(GetInfoUser.getUsername(), 0);

                List<ShoppingCartDetail> shoppingCartDetails = shoppingCartNew.getShoppingCartDetails();

                boolean checkProductExists=false;
                // chech sản phẩm đã ở trong giỏ hàng thì tăng số lượng
                for (ShoppingCartDetail a : shoppingCartDetails) {
                    if (Objects.equals(a.getProduct().getId(), shoppingCartDto.getIdProduct())) {
                        a.setQuantityCart(a.getQuantityCart() + shoppingCartDto.getQuantityCart());
                        checkProductExists=true;
                        break;
                    }
                }
                if(!checkProductExists) {
                    // tạo 1 shoppingCartDetail
                    ShoppingCartDetail shoppingCartDetail = new ShoppingCartDetail();
                    // set số lượng mua sản phẩm từ fe
                    shoppingCartDetail.setQuantityCart(shoppingCartDto.getQuantityCart());
                    //set time hiện tai
                    shoppingCartDetail.setDateCreated(new Date());
                    // set product từ id product  fe trả về
                    shoppingCartDetail.setProduct(productRepository.findById(shoppingCartDto.getIdProduct()).orElse(null));

                    // thêm shoppingCartDetail mới vào shoppingCartNew.getShoppingCartDetails()
                    shoppingCartDetails.add(shoppingCartDetail);
                }
                shoppingCartNew.setShoppingCartDetails(shoppingCartDetails);

            } else {
                shoppingCartNew.setPaymentDate(new Date());
                //Status =0 ;dang nằm trong giỏ hàng của User
                shoppingCartNew.setStatus(0);
                shoppingCartNew.setUser(userRepository.findByUsername(GetInfoUser.getUsername())); //shoppingCartDto

                // tạo 1 shoppingCartDetail
                ShoppingCartDetail shoppingCartDetail = new ShoppingCartDetail();
                // set số lượng mua sản phẩm từ fe
                shoppingCartDetail.setQuantityCart(shoppingCartDto.getQuantityCart());
                //set time hiện tai
                shoppingCartDetail.setDateCreated(new Date());
                // set product từ id product  fe trả về
                shoppingCartDetail.setProduct(productRepository.findById(shoppingCartDto.getIdProduct()).orElse(null));
                // tạo 1 list shoppingCartDetails để thêm 1 shoppingCartDetail
                List<ShoppingCartDetail> shoppingCartDetails = new ArrayList<>();
                shoppingCartDetails.add(shoppingCartDetail);

                // set shoppingCartNew.setShoppingCartDetails để thêm vào db
                shoppingCartNew.setShoppingCartDetails(shoppingCartDetails);
            }
            shoppingCartRepository.save(shoppingCartNew);
            messenger.setMessenger("Add to cart successfully");
            return new ResponseEntity<>(shoppingCartNew, HttpStatus.OK);
        } catch (Exception e) {
            messenger.setMessenger("error");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<?> getAll() {
//        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
//        List<ShoppingCartDtoReturn> shoppingCartDtoReturns1 = new ArrayList<>();
//
//        List<ShoppingCartDtoReturn> shoppingCartDtoReturns = shoppingCarts.stream()
//                .map(ShoppingCartDtoReturn::new)
//                .collect(Collectors.toList());

        return new ResponseEntity<>(null, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> delteteByIdShoppingCartDetail(Long id) {
        if (shoppingCartRepository.existsByUser_UsernameAndShoppingCartDetails_Id(GetInfoUser.getUsername(),id))
        {
            shoppingCartDetailRepository.deleteById(id);
            messenger.setMessenger("Xóa sản phẩm thành công");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        }
        messenger.setMessenger("Sảm phẩm không tồn tại");
        return new ResponseEntity<>(messenger, HttpStatus.NOT_FOUND);
    }
    @Override
    public ResponseEntity<?> getCartByUser() {
        if(shoppingCartRepository.existsByUser_Username(GetInfoUser.getUsername())) {
            ShoppingCart shoppingCarts = shoppingCartRepository.findByUsernameAndStatus(GetInfoUser.getUsername(), 0);
            ShoppingCartDtoReturn shoppingCartDtoReturns = new ShoppingCartDtoReturn(shoppingCarts);
            return new ResponseEntity<>(shoppingCartDtoReturns, HttpStatus.OK);
        }
        messenger.setMessenger("Chữa có gì trong giỏ hàng");
        return new ResponseEntity<>(messenger, HttpStatus.NO_CONTENT);
    }
}
