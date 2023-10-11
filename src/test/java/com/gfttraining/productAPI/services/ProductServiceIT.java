package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ProductServiceIT {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductServiceIT(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Test
    public void productCreationTest() {
        
        int size = productRepository.findByName("paper").size();

        assertEquals(0, size);

        productService.createProduct("paper", "sheet of paper", "office", 4.99, 10);

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
