package com.gfttraining.productAPI.config;


import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

@Component
public class ProductInitializer implements CommandLineRunner {
    private static Logger logger = Logger.getLogger(ProductInitializer.class);
    private final ProductService productService;

    public ProductInitializer(ProductService productService){
        this.productService = productService;
    }

    @Override
    public void run(String... args) {

        productService.createProducts(
            Arrays.asList(
                new ProductRequest("Cookies", "Chocolate cookies", "food", 9.99, 10,1.0),
                new ProductRequest("Book", "Small book", "books", 5.0, 20,1.0),
                new ProductRequest("Desk", "Big desk", "furniture", 9.99, 1,1.0)
            )
        );
        logger.info(" Products DDBB was initialized  ");
    }
        
}
