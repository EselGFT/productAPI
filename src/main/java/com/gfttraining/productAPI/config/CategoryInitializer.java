package com.gfttraining.productAPI.config;

import java.util.Arrays;

import com.gfttraining.productAPI.services.ProductService;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;

@Component
public class CategoryInitializer implements CommandLineRunner{
    private static Logger logger = Logger.getLogger(CategoryInitializer.class);
    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        
        categoryRepository.saveAll(
            Arrays.asList(
                new Category("toys", 20.0),
                new Category("books", 15.0),
                new Category("sports", 5.0),
                new Category("food", 25.0),
                new Category("clothes", 35.0),
                new Category("other", 0.0)
            )
        );
        logger.info(" Category DDBB was initialized  ");
    }
    
}
