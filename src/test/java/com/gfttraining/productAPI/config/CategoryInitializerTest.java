package com.gfttraining.productAPI.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;


public class CategoryInitializerTest {
    
    private final CategoryRepository categoryRepository;

    public CategoryInitializerTest(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Test
    public void initializerMethodTest() {
        Category category = categoryRepository.findById("toys").get();

        assertEquals("toys", category.getName());
        assertEquals(20.0, category.getDiscount());
    }
}
