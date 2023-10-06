package com.gfttraining.productAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;

@Service
public class ProductService {
    
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;
    // private final CategoryRepository categoryRepository;
    // private final ProductRepository productRepository;
    
    // public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository){
        
    //     this.productRepository = productRepository;
    //     this.categoryRepository = categoryRepository;
    // }

    public void createProduct(String name, String description, String categoryName, Double price, int stock) {

        Category category = categoryRepository.findById(categoryName).orElse(categoryRepository.findById("other").get());

        Product product = new Product(name, description, category, price, stock);
        productRepository.save(product);
    }
}
