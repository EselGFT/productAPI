package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.ProductRepository;

@SpringBootTest
public class ProductServiceIT {
    
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("GIVEN an initial amount of products WHEN a new one is created THEN the number of products increases by 1")
    public void productCreationTest() {
    
        int size = productRepository.findByNameIgnoreCaseContaining("paper").size();

        assertEquals(0, size);

        productService.createProduct(new ProductRequest("paper", "sheet of paper", "office", 4.99, 10, 1.0));

        int newSize = productRepository.findByNameIgnoreCaseContaining("paper").size();

        assertEquals(1, newSize);

    }

    // start of listProductById() tests

    @Test
    @DisplayName("WHEN listing a created product GIVEN its ID THEN the created product is returned")
    public void listProductTest() throws NonExistingProductException {

        productService.createProduct(new ProductRequest("Apple", "A rounded food object", "food", 1.25, 23,1.0));

        int numberOfProducts = productRepository.findAll().size();

        Product found = productService.listProductById(numberOfProducts);

        assertEquals(found.getName(), "Apple");
        assertEquals(found.getDescription(), "A rounded food object");
    }

    // end of listProductById() tests

    @Test
    @DisplayName("WHEN we get all the products from the service and the repository THEN both lists should be the same size")
    void listProductsTest() {
        int existingProducts = productRepository.findAll().size();
        int productsToList = productService.listProducts().size();

        assertEquals(existingProducts, productsToList);
    }

}
