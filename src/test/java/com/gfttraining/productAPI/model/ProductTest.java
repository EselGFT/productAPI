package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    public void productCreationTest() {
        Product product = new Product("Cookies", "Chocolate cookies", new Category("food",25.0), 9.99, 10);
        
        assertEquals(product.getCategory().getName(), "food");
    }
}
