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
    public ResponseEntity<?> payment() {

        if (shoppingCartRepository.existsByUser_UsernameAndStatus(GetInfoUser.getUsername(), 0)) {
            // update ShoppingCart with status =1
            ShoppingCart shoppingCartNew = shoppingCartRepository.findByUsernameAndStatus(GetInfoUser.getUsername(), 0);
            shoppingCartNew.setStatus(1);

            //Trừ số lượng ở kho
            for (ShoppingCartDetail a:shoppingCartNew.getShoppingCartDetails()){
                Product product = productRepository.findById(a.getProduct().getId()).orElse(null);
                assert product != null;
                product.setQuantity(product.getQuantity()-a.getQuantityCart());

                productRepository.save(product);
            }

            shoppingCartRepository.save(shoppingCartNew);

            messenger.setMessenger("Đặt hàng thành công. ");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        else
        {
            messenger.setMessenger("Giỏ hàng rỗng.");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> putCart(ShoppingCartDto shoppingCartDto) {
        return null;
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

                // Tạo một biến để kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
                boolean productExists = false;

                // Duyệt qua danh sách shoppingCartDetails
                for (ShoppingCartDetail cartDetail : shoppingCartDetails) {
                    // Kiểm tra xem sản phẩm trong chi tiết giỏ hàng có cùng id với sản phẩm cần thêm vào giỏ hàng không
                    if (Objects.equals(cartDetail.getProduct().getId(), shoppingCartDto.getIdProduct())) {
                        // Kiểm tra xem số lượng trong kho có đủ để thêm vào giỏ hàng không
                        long availableQuantity = cartDetail.getProduct().getQuantity();
                        long requestedQuantity = cartDetail.getQuantityCart() + shoppingCartDto.getQuantityCart();
                        if (availableQuantity < requestedQuantity) {
                            messenger.setMessenger("Số lượng đã đến giới hạn. ");
                            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
                        }

                        // Cập nhật số lượng trong giỏ hàng
                        cartDetail.setQuantityCart(requestedQuantity);
                        productExists = true;
                        break; // Kết thúc vòng lặp vì đã tìm thấy sản phẩm trong giỏ hàng
                    }
                }
                if(!productExists) {
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
            messenger.setMessenger("Thêm thành công");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } catch (Exception e) {
            messenger.setMessenger("error");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        List<ShoppingCartDtoReturn> shoppingCartDtoReturns = shoppingCarts.stream()
                .map(ShoppingCartDtoReturn::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(shoppingCartDtoReturns, HttpStatus.OK);

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
        try{
            if(shoppingCartRepository.existsByUser_UsernameAndStatus(GetInfoUser.getUsername(),0)) {
                ShoppingCart shoppingCarts = shoppingCartRepository.findByUsernameAndStatus(GetInfoUser.getUsername(), 0);
                ShoppingCartDtoReturn shoppingCartDtoReturns = new ShoppingCartDtoReturn(shoppingCarts);
                return new ResponseEntity<>(shoppingCartDtoReturns, HttpStatus.OK);
            }
            messenger.setMessenger("Chưa có gì trong giỏ hàng");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        }
        catch (Exception e){
            messenger.setMessenger("Đã xảy ra lỗi");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }

    }


}
