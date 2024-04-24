package com.example.demo.service.serviceImpl;


import com.example.demo.Util.GetInfoUser;
import com.example.demo.model.Dto.Messenger;
import com.example.demo.model.Dto.ShoppingCartDto;
import com.example.demo.model.Dto.ShoppingCartDtoReturn;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ShoppingCart;
import com.example.demo.model.entity.ShoppingCartDetail;
import com.example.demo.repositories.*;
import com.example.demo.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Date;

import static com.example.demo.Util.GetInfoUser.getUsername;

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
    public ResponseEntity<?> paymentCart() {

        if (shoppingCartRepository.existsByUser_UsernameAndStatus(getUsername(), 0)) {
            // update ShoppingCart with status =1
            ShoppingCart shoppingCartNew = shoppingCartRepository.findByUsernameAndStatus(getUsername(), 0);

            List<StringBuilder> mess = new ArrayList<>();
            for (ShoppingCartDetail cartDetail : shoppingCartNew.getShoppingCartDetails()) {
                // Kiểm tra xem số lượng trong kho có đủ để thêm vào giỏ hàng không
                long availableQuantity = cartDetail.getProduct().getQuantity();
                long requestedQuantity = cartDetail.getQuantityCart();

                if (availableQuantity < requestedQuantity) {
                    StringBuilder mess1 = new StringBuilder();
                    mess1.append("Sản phẩm ");
                    mess1.append(cartDetail.getProduct().getName());
                    mess1.append(" đã hết hàng.");
                    mess.add(mess1);
                    return new ResponseEntity<>(mess, HttpStatus.BAD_REQUEST);
                }
            }
            shoppingCartNew.setStatus(1);

            //Trừ số lượng ở kho
            for (ShoppingCartDetail a : shoppingCartNew.getShoppingCartDetails()) {
                Product product = productRepository.findById(a.getProduct().getId()).orElse(null);
                assert product != null;
                product.setQuantity(product.getQuantity() - a.getQuantityCart());

                productRepository.save(product);
            }

            shoppingCartRepository.save(shoppingCartNew);

            messenger.setMessenger("Đặt hàng thành công. ");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } else {
            messenger.setMessenger("Giỏ hàng rỗng.");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> updateCart(Long idCart, int quantityCart) {
        // Check if the item in the cart exists
        if (!shoppingCartRepository.existsByUser_UsernameAndShoppingCartDetails_Id(GetInfoUser.getUsername(), idCart)) {
            messenger.setMessenger("Sản phẩm không tồn tại. ");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }

        // Retrieve the cart from the database
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser_UsernameAndShoppingCartDetails_Id(GetInfoUser.getUsername(), idCart);

        // Delete the product if the update quantity is 0 or less
        if (quantityCart <= 0) {
            return deleteByIdShoppingCartDetail(idCart);
        }

        // Get the cart details
        List<ShoppingCartDetail> shoppingCartDetails = shoppingCart.getShoppingCartDetails();

        // Iterate through the cart details to update the quantity
        for (ShoppingCartDetail cartDetail : shoppingCartDetails) {
            if (Objects.equals(cartDetail.getId(), idCart)) {
                // Check if the quantity in stock is sufficient for update
                long availableQuantity = cartDetail.getProduct().getQuantity();
                if (availableQuantity < quantityCart) {
                    messenger.setMessenger("Số lượng đã đến giới hạn. ");
                    return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
                }
                // Update the quantity in the cart
                cartDetail.setQuantityCart(quantityCart);
                break;
            }
        }
        // Save the update to the database
        shoppingCart.setShoppingCartDetails(shoppingCartDetails);
        shoppingCartRepository.save(shoppingCart);

        messenger.setMessenger("Cập nhật thành công");
        return new ResponseEntity<>(messenger, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addCart(ShoppingCartDto shoppingCartDto) {
        try {
            if (shoppingCartDto.getQuantityCart() <= 0) {
                messenger.setMessenger("Số lượng phải lớn hơn 0. ");
                return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra xem sản phẩm có tồn tại không
            Product product = productRepository.findById(shoppingCartDto.getIdProduct()).orElse(null);
            if (product == null) {
                messenger.setMessenger("Sản phẩm không tồn tại");
                return new ResponseEntity<>(messenger, HttpStatus.NOT_FOUND);
            }

            // Kiểm tra số lượng tồn kho
            long availableQuantity = product.getQuantity();
            long requestedQuantity = shoppingCartDto.getQuantityCart();
            if (availableQuantity < requestedQuantity) {
                messenger.setMessenger("Số lượng đã đến giới hạn. ");
                return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
            }

            // Tạo shopping cart mới nếu chưa tồn tại
            ShoppingCart shoppingCartNew = shoppingCartRepository.findByUser_UsernameAndStatus(getUsername(), 0).orElse(null);
            if (shoppingCartNew == null) {
                shoppingCartNew = new ShoppingCart();
                shoppingCartNew.setPaymentDate(new Date());
                shoppingCartNew.setStatus(0);
                shoppingCartNew.setUser(userRepository.findByUsername(getUsername()));
            }

            // Tạo hoặc cập nhật chi tiết giỏ hàng
            List<ShoppingCartDetail> shoppingCartDetails = shoppingCartNew.getShoppingCartDetails();
            boolean productExists = false;
            for (ShoppingCartDetail cartDetail : shoppingCartDetails) {
                if (Objects.equals(cartDetail.getProduct().getId(), shoppingCartDto.getIdProduct())) {
                    long updatedQuantity = cartDetail.getQuantityCart() + shoppingCartDto.getQuantityCart();
                    if (updatedQuantity > availableQuantity) {
                        messenger.setMessenger("Số lượng đã đến giới hạn. ");
                        return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
                    }
                    cartDetail.setQuantityCart(updatedQuantity);
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                ShoppingCartDetail shoppingCartDetail = new ShoppingCartDetail();
                shoppingCartDetail.setQuantityCart(shoppingCartDto.getQuantityCart());
                shoppingCartDetail.setDateCreated(new Date());
                shoppingCartDetail.setProduct(product);
                shoppingCartDetails.add(shoppingCartDetail);
            }

            shoppingCartNew.setShoppingCartDetails(shoppingCartDetails);
            shoppingCartRepository.save(shoppingCartNew);
            messenger.setMessenger("Thêm thành công");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } catch (Exception e) {
            messenger.setMessenger("Lỗi xảy ra khi thêm vào giỏ hàng");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> getAllCard() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        List<ShoppingCartDtoReturn> shoppingCartDtoReturns = shoppingCarts.stream()
                .map(ShoppingCartDtoReturn::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(shoppingCartDtoReturns, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> deleteByIdShoppingCartDetail(Long id) {
        if (shoppingCartRepository.existsByUser_UsernameAndShoppingCartDetails_Id( getUsername() , id)) {
            shoppingCartDetailRepository.deleteById(id);
            messenger.setMessenger("Xóa sản phẩm thành công");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        }
        messenger.setMessenger("Sảm phẩm không tồn tại");
        return new ResponseEntity<>(messenger, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> getCartByUser() {
        try {
            if (shoppingCartRepository.existsByUser_UsernameAndStatus(getUsername(), 0)) {
                ShoppingCart shoppingCarts = shoppingCartRepository.findByUsernameAndStatus(getUsername(), 0);
                ShoppingCartDtoReturn shoppingCartDtoReturns = new ShoppingCartDtoReturn(shoppingCarts);
                return new ResponseEntity<>(shoppingCartDtoReturns, HttpStatus.OK);
            }
            messenger.setMessenger("Chưa có gì trong giỏ hàng");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } catch (Exception e) {
            messenger.setMessenger("Đã xảy ra lỗi");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }

    }

}
