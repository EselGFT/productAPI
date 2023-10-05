package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryTest {
    
    @Test
    @DisplayName("When a category is created, then the values should be equal to the assigned ones")
    public void createCategoryTest(){
        Category category1 = new Category("toys", 20.0);

        assertEquals("toys", category1.getName());
        assertEquals(20.0, category1.getDiscount());
    }
    
}
