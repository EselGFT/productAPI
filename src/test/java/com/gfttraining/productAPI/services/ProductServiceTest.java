package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.repositories.ProductRepository;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Test
    public void productCreationTest() {
        
        int size = productRepository.findByName("paper").size();

        assertEquals(0, size);

        productService.createProduct("paper", "sheet of paper", "office", 4.99, 10);

        int newSize = productRepository.findByName("paper").size();

        assertEquals(1, newSize);

    }
    // @Test
    // public void productCreationTest() {
        
    //     int size = productRepository.findByName("paper").size();

    //     assertEquals(0, size);

    //     productService.createProduct("paper", "sheet of paper", "office", 4.99, 10);

    //     int newSize = productRepository.findByName("paper").size();

    //     assertEquals(1, newSize);

    // }


}
