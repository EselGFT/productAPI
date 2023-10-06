package com.gfttraining.productAPI.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;

import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;

@Component
public class ProductInitializer implements CommandLineRunner {
    
    private final ProductService productService;

    public ProductInitializer(ProductService productService){
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
         productService.createProduct("Cookies", "Chocolate cookies", "food", 9.99, 10);
         productService.createProduct("Librito", "Small book", "books", 5.0, 20);
         productService.createProduct("Joel", "sports man", "person", 9.99, 1);
    }
        
}
