package com.gfttraining.productAPI.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;

@SpringBootTest
public class CategoryInitializerTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void initializerMethodTest() {
        Category category = categoryRepository.findById("toys").get();

        assertEquals("toys", category.getName());
        assertEquals(20.0, category.getDiscount());
    }
}
