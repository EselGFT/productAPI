package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryTest {
    
    @Test
    @DisplayName("When a category is created, Then values should be set correctly")
    public void categoryConstructorTest(){
        Category category1 = new Category("toys", 20.0);

        assertEquals("toys", category1.getName());
        assertEquals(20.0, category1.getDiscount());
    }
    
    @Test
    @DisplayName("When a category is created with the default constructor, Then default values should be set")
    public void categoryDefaultConstructorTest(){
        Category category1 = new Category();

        assertNull(category1.getName());
        assertEquals(0.0, category1.getDiscount());
    }
}
