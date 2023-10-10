package com.gfttraining.productAPI.services;

import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;

@Service
public class ProductService {
    
    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository){
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;

    }

    public Product createProduct(String name, String description, String categoryName, Double price, int stock) {

        Category category = categoryRepository.findById(categoryName).orElse(categoryRepository.findById("other").get());
        
        Product product = new Product(name, description, category, price, stock);
        
        return productRepository.save(product);
        
    }


}
