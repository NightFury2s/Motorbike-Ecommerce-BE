package com.example.demo.controller;

import com.example.demo.model.Dto.ProductDto;

import com.example.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RequestMapping("/admin/productcar")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //@RequestParam("productImage") MultipartFile fileProductImage
    @PostMapping("/admin/productcar/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto) {
        return productService.add(productDto);
    }

    // get by idType
    @GetMapping("/productcar/getsome/{page}/{size}/{idType}")
    public ResponseEntity<?> getSomeProduct(@PathVariable int page, @PathVariable int size, @PathVariable Long idType) {
        return productService.getSome(page, size, idType);
    }


    @GetMapping("/productcar/getTypeDetail/{page}/{size}/{detailType}")
    public ResponseEntity<?> getTypeDetail(@PathVariable int page, @PathVariable int size, @PathVariable Long detailType) {
        return productService.getTypeDetail(page, size, detailType);
    }
    //Get by details Sort by price
    @GetMapping("/productcar/getTypeDetail/{page}/{size}/{detailType}/{arrange}")
    public ResponseEntity<?> getTypeDetailArrange(@PathVariable int page, @PathVariable int size, @PathVariable Long detailType, @PathVariable String arrange) {
        return productService.getTypeDetailArrange(page, size, detailType, arrange);
    }

    //get by detailType Sort by price
    @GetMapping("/productcar/getsome/{page}/{size}/{typeProduct_Id}/{arrange}")
    public ResponseEntity<?> getByTypeProductIdArrange(@PathVariable int page, @PathVariable int size, @PathVariable Long typeProduct_Id, @PathVariable String arrange) {
        return productService.getByTypeProduct_IdArrange(page, size, typeProduct_Id, arrange);
    }

    //product details
    @GetMapping("/product/getDetail/{idProduct}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long idProduct) {
        return productService.getDetail(idProduct);
    }
    @GetMapping("/productcar/find-by-name-product/{page}/{size}/{nameProduct}")
    public ResponseEntity<?> findByNameProduct(@PathVariable int page, @PathVariable int size, @PathVariable String nameProduct) {
        return productService.findByNameProduct(page, size, nameProduct);
    }

    @DeleteMapping("/admin/product/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        return productService.delete(id);
    }
    @DeleteMapping("/admin/product/delete-multiple-products")
    public ResponseEntity<?> deleteMultipleProducts(@RequestBody List<Long> ids) {
        return productService.deleteMultipleProducts(ids);
    }

    @PutMapping("/admin/product/put/{id}")
    public ResponseEntity<?> put(@PathVariable long id, @RequestBody ProductDto productDto) {
        return productService.put(id, productDto);
    }
}
