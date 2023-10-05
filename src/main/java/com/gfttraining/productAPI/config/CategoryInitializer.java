package com.gfttraining.productAPI.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.repositories.CategoryRepository;

@Component
public class CategoryInitializer implements CommandLineRunner{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Category> categories = new ArrayList<Category>(
            Arrays.asList(
                new Category("toys", 20.0),
                new Category("books", 15.0),
                new Category("sports", 5.0),
                new Category("food", 25.0),
                new Category("clothes", 35.0),
                new Category("other", 0.0)
                )
            
        );
        
        categoryRepository.saveAll(categories);
    }
    
}
