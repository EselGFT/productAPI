package com.gfttraining.productAPI.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;

import com.gfttraining.productAPI.repositories.ProductRepository;

@Component
public class ProductInitializer implements CommandLineRunner {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Product> products = new ArrayList<Product>(
            Arrays.asList(
                new Product("Cookies", "Chocolate cookies", new Category("food",25.0), 9.99, 10),
                new Product("Librito", "Small book", new Category("books",15.0), 9.99, 10),
                new Product("Joel", "Sports man", new Category("person",99.0), 9.99, 10)
                )
            
        );
        
        productRepository.saveAll(products);
    }
        
}
