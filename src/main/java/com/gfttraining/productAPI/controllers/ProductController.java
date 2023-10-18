package com.gfttraining.productAPI.controllers;

import java.util.List;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import com.gfttraining.productAPI.exceptions.NotAllProductsFoundException;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductResponse;
import com.gfttraining.productAPI.services.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

    
    private final ProductService productService;   

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        return new ResponseEntity<>(
                productService.listProducts(),
                HttpStatus.OK
        );
    }

    @GetMapping("/products/{id_product}")
    public ResponseEntity<Product> getProductById(@PathVariable int id_product) throws NonExistingProductException {

        return new ResponseEntity<>(
                productService.listProductById(id_product),
                HttpStatus.OK
        );
    }

    @PostMapping("/product")
    public ResponseEntity<Product> postMapping(@RequestBody @Valid ProductRequest productRequest)
    {
        return new ResponseEntity<>( 
                productService.createProduct(productRequest),
                HttpStatus.OK
            );        
    }


    @PostMapping("/products")
    public ResponseEntity<List<Product>> postLoadProducts(@RequestBody @Valid List<ProductRequest> productRequests) {
        return new ResponseEntity<>(
            productService.createProducts(productRequests),
            HttpStatus.OK
        );
    }

    @PutMapping("/products/{id_product}")
    public ResponseEntity<Product> putUpdate (@PathVariable int id_product,  @RequestBody @Valid ProductRequest updateProductRequest) throws NonExistingProductException {
        return new ResponseEntity<>(
            productService.updateProduct(id_product, updateProductRequest),
            HttpStatus.OK
            );
    }

    @DeleteMapping("/products/{id_product}")
    public ResponseEntity <?> deleteProduct (@PathVariable long id_product) throws NonExistingProductException  {
        productService.deleteProduct(id_product);
        return new ResponseEntity<>(
                HttpStatus.OK
        );

    }
    @PostMapping("/productsWithIDs")
   public ResponseEntity<List<ProductResponse>> productsWithIDs (@RequestBody List<Long> ids) throws NotAllProductsFoundException {
        return new ResponseEntity<>(
            productService.listProductsWithIDs(ids),
            HttpStatus.OK
        );
    }

}
