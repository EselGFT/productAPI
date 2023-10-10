package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductRequestTest {

    @Test
    @DisplayName("When a product request is created, Then values should be set correctly")
    public void productRequestConstructorTest() {
        
        ProductRequest productRequest = new ProductRequest("Laptop", "Powerful laptop", "laptop", 999.99, 10);

        assertEquals("Laptop", productRequest.getName());
        assertEquals("Powerful laptop", productRequest.getDescription());
        assertEquals("laptop", productRequest.getCategory());
        assertEquals(999.99, productRequest.getPrice()); 
        assertEquals(10, productRequest.getStock());
    
    }

    @Test
    @DisplayName("When a product request is created with the default constructor, Then default values should be set")
    public void productRequestDefaultConstructorTest() {

        ProductRequest productRequest = new ProductRequest();

        assertNull(productRequest.getName());
        assertNull(productRequest.getDescription());
        assertNull(productRequest.getCategory());
        assertNull(productRequest.getPrice());
        assertEquals(0, productRequest.getStock());
    }

}
