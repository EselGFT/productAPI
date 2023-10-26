package com.gfttraining.productAPI.controllers;

import java.util.List;

import com.gfttraining.productAPI.ProductApiApplication;
import jakarta.validation.Valid;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;

import com.gfttraining.productAPI.model.ProductToSubmit;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductDTO;
import com.gfttraining.productAPI.services.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class ProductController {

    private static Logger logger = Logger.getLogger(ProductController.class);
    private final ProductService productService;   

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // start of /products root uri
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        logger.info(" Get_List_Product starts ");
        return new ResponseEntity<>(
                productService.listProducts(),
                HttpStatus.OK
        );
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        logger.info(" Post_Create_Product starts ");
        return new ResponseEntity<>(
                productService.createProduct(productRequest),
                HttpStatus.OK
        );
    }

    @GetMapping("/products/{product_id}")
    public ResponseEntity<Product> getProductById(@PathVariable int product_id) throws NonExistingProductException {
        logger.info("******** Get_Product_By_Id starts *****");
        return new ResponseEntity<>(
                productService.listProductById(product_id),
                HttpStatus.OK
        );
    }

    @PutMapping("/products/{product_id}")
    public ResponseEntity<Product> updateProduct(@PathVariable long product_id, @RequestBody @Valid ProductRequest updateProductRequest) throws NonExistingProductException, InvalidCartConnectionException, InvalidCartResponseException {
        logger.info("Put Update Product starts ");
        return new ResponseEntity<>(
                productService.updateProduct(product_id, updateProductRequest),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/products/{product_id}")
    public ResponseEntity <?> deleteProduct(@PathVariable long product_id) throws NonExistingProductException  {
        logger.info("Delete Delete Product starts ");
        productService.deleteProduct(product_id);

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PostMapping("/products/bulkCreate")
    public ResponseEntity<List<Product>> bulkCreateProducts(@RequestBody  List<@Valid ProductRequest> productRequests) {
        logger.info("Post Bulk Create Products Product starts ");
        return new ResponseEntity<>(
                productService.createProducts(productRequests),
                HttpStatus.OK
        );
    }

    @PostMapping("/products/getBasicInfo")
    public ResponseEntity<List<ProductDTO>> getProductsBasicInfo(@RequestBody List<Long> ids) throws NonExistingProductException {
        logger.info("Post Get Products Basic Info starts ");
        return new ResponseEntity<>(
                productService.createProductResponsesWithProductIDs(ids),
                HttpStatus.OK
        );
    }

    @PostMapping("/products/reduceStock")
    public ResponseEntity<List<ProductDTO>> reduceStock(@RequestBody List<ProductToSubmit> productsToSubmit) throws NonExistingProductException, NotEnoughStockException {
        logger.info("Post Reduce Product Stock  starts ");
        return new ResponseEntity<>(
                productService.checkIfEnoughStockAndSubtract(productsToSubmit),
                HttpStatus.OK
        );
    }

    @GetMapping("/products/search/{product_name}")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable String product_name) {
        logger.info("Get Search Product By Name  starts ");
        return new ResponseEntity<>(
                productService.listProductsByNameContainsIgnoreCase(product_name),
                HttpStatus.OK
        );
    }

    // end of /products root uri
}
