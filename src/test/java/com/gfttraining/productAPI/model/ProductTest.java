package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class ProductTest {

    Category category;

    @BeforeEach
    public void setUp(){
        this.category = new Category("Electronics", 25.0);
    }

    @Test
    @DisplayName("When a product is created, Then values should be set correctly")
    public void productConstructorTest() {
        
        Product product = new Product("Laptop", "Powerful laptop", category, 999.99, 10);

        assertEquals("Laptop", product.getName());
        assertEquals("Powerful laptop", product.getDescription());
        assertEquals(category, product.getCategory());
        assertEquals(999.99, product.getPrice()); 
        assertEquals(10, product.getStock());
    
    }

    @Test
    @DisplayName("When a product is created with the default constructor, Then default values should be set")
    public void productDefaultConstructorTest() {

        Product product = new Product();

        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getCategory());
        assertNull(product.getPrice());
        assertEquals(0, product.getStock());
    }

    // @Test
    // public void testProductIdGeneration() {
        
    //     Product product1 = new Product("LaptopTest1", "Powerful laptop", category, 999.99, 10);
    //     Product product2 = new Product("LaptopTest2", "Powerful laptop", category, 999.99, 10);

    //     // assertEquals(0, product1.getId());
    //     // assertEquals(1, product2.getId());
    //     assertNotEquals(product1.getId(), product2.getId());
    // }

    
}
