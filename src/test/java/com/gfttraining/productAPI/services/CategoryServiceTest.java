package com.gfttraining.productAPI.services;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.categoryService = new CategoryService(categoryRepository);
    }

    @Test
    @DisplayName("")
    void getCategoryByNameTest() {
        Category food = new Category("food", 25.0);
        Category other = new Category("other", 0.0);

        Mockito.when(categoryRepository.findById("food")).thenReturn(Optional.of(food));
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));

        Category found = categoryService.getCategoryByName("food");

        assertEquals(food, found);
        assertEquals(food.getName(), found.getName());
        assertEquals(food.getDiscount(), found.getDiscount());
    }

    @Test
    @DisplayName("")
    void getNonExistentCategoryByNameTest() {
        Category other = new Category("other", 0.0);

        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));

        Category found = categoryService.getCategoryByName("puzzle");

        assertEquals(other, found);
        assertEquals(other.getName(), found.getName());
        assertEquals(other.getDiscount(), found.getDiscount());
    }
}
