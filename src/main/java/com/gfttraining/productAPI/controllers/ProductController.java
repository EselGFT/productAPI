package com.gfttraining.productAPI.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

@RestController
public class ProductController {

    
    private final ProductService productService;   

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<Product> postMapping(@RequestBody ProductRequest productRequest){
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>( 
                productService.createProduct(
                    productRequest.getName(),
                    productRequest.getDescription(),
                    productRequest.getCategory(),
                    productRequest.getPrice(),
                    productRequest.getStock()),
                headers,   
                HttpStatus.OK
            );        
    }

    @PostMapping("/products")
    public ResponseEntity<List<Product>> postLoadProducts(@RequestBody List<ProductRequest> productRequests) {
        HttpHeaders header = new HttpHeaders();
        return new ResponseEntity<>(
            productService.createProducts(productRequests),
            header,
            HttpStatus.OK
        );
    }

}
