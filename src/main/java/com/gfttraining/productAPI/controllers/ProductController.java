package com.gfttraining.productAPI.controllers;

import java.util.List;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import com.gfttraining.productAPI.model.ProductToSubmit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductDTO;
import com.gfttraining.productAPI.services.ProductService;

import jakarta.validation.Valid;

@RestController
@Validated
public class ProductController {

    
    private final ProductService productService;   

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // start of /products root uri
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        return new ResponseEntity<>(
                productService.listProducts(),
                HttpStatus.OK
        );
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        return new ResponseEntity<>(
                productService.createProduct(productRequest),
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

    @PutMapping("/products/{id_product}")
    public ResponseEntity<Product> updateProduct(@PathVariable long id_product, @RequestBody @Valid ProductRequest updateProductRequest) throws NonExistingProductException {
        return new ResponseEntity<>(
                productService.updateProduct(id_product, updateProductRequest),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/products/{id_product}")
    public ResponseEntity <?> deleteProduct(@PathVariable long id_product) throws NonExistingProductException  {
        productService.deleteProduct(id_product);

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PostMapping("/products/bulkCreate")
    public ResponseEntity<List<Product>> bulkCreateProducts(@RequestBody  List<@Valid ProductRequest> productRequests) {
        return new ResponseEntity<>(
                productService.createProducts(productRequests),
                HttpStatus.OK
        );
    }

    @PostMapping("/products/getBasicInfo")
    public ResponseEntity<List<ProductDTO>> getProductsBasicInfo(@RequestBody List<Long> ids) throws NonExistingProductException {
        return new ResponseEntity<>(
                productService.createProductResponsesWithProductIDs(ids),
                HttpStatus.OK
        );
    }

    @PostMapping("/products/reduceStock")
    public ResponseEntity<List<ProductDTO>> reduceStock(@RequestBody List<ProductToSubmit> productsToSubmit) throws NonExistingProductException, NotEnoughStockException {
        return new ResponseEntity<>(
                productService.checkIfEnoughStockAndSubtract(productsToSubmit),
                HttpStatus.OK
        );
    }

    @GetMapping("/products/search/{product_name}")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable String product_name) {
        return new ResponseEntity<>(
                productService.listProductsByNameContainsIgnoreCase(product_name),
                HttpStatus.OK
        );
    }

    // end of /products root uri
}
