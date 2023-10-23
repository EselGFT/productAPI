package com.gfttraining.productAPI.services;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findById(name)
            .orElse(categoryRepository.findById("other").get());
    }
}
