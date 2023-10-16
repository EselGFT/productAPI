package com.gfttraining.productAPI.config;


import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

@Component
public class ProductInitializer implements CommandLineRunner {
    
    private final ProductService productService;

    public ProductInitializer(ProductService productService){
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ProductRequest> productRequests = Arrays.asList(
            new ProductRequest("Cookies", "Chocolate cookies", "food", 9.99, 10,1.0),
            new ProductRequest("Book", "Small book", "books", 5.0, 20,1.0),
            new ProductRequest("Desk", "Big desk", "furniture", 9.99, 1,1.0)
        );

        productService.createProducts(productRequests);
    }
        
}
