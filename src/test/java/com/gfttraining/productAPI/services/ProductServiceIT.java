package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.gfttraining.productAPI.repositories.ProductRepository;

@SpringBootTest
public class ProductServiceIT {
    
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    @Test
    public void productCreationTest() {
    
        int size = productRepository.findByName("paper").size();

        assertEquals(0, size);

        productService.createProduct("paper", "sheet of paper", "office", 4.99, 10, 1.0);

        int newSize = productRepository.findByName("paper").size();

        assertEquals(1, newSize);

    }

    @Test
    void listProductsTest() {
        int existingProducts = productRepository.findAll().size();
        int productsToList = productService.listProducts().size();

        assertEquals(existingProducts, productsToList);
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
