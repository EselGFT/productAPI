package com.gfttraining.productAPI.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
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

    public Product createProduct(String name, String description, String categoryName, Double price, int stock, Double weight) {

        Category category = categoryRepository.findById(categoryName).orElse(categoryRepository.findById("other").get());
        
        Product product = new Product(name, description, category, price, stock, weight);
        
        return productRepository.save(product);
        
    }
    
    public Product updateProduct (int id,String name, String description, String categoryName, Double price, int stock,Double productWeight){
    	    	 
    	Category category = categoryRepository.findById(categoryName).orElse(categoryRepository.findById("other").get());
    	
    	Product productUpdate = productRepository.findById(id).get();
    	if (name != null) {
    		productUpdate.setName(name);    		
    	} 
    	if (description != null) {
    		productUpdate.setDescription(description);    		
    	}
    	if (categoryName != null) {
    		productUpdate.setCategory(category);    		
    	}
    	if (price != null) {
    		productUpdate.setPrice(price);    		
    	}

    	if(stock != 0){
    		productUpdate.setStock(stock);    		
    	}
        if (productWeight != null) {
            productUpdate.setWeight(productWeight);
        }
    	
    	return productRepository.save(productUpdate);
    	
    	
    	
    }
    
    /*
    public List<Product> products () {
    	return productRepository.findAll();
    }
    
    public Product productById (@PathVariable int id) {
    	return productRepository.findById(id).get();
    	
    }*/

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(pr -> createProduct(pr.getName(), pr.getDescription(), pr.getCategory(), pr.getPrice(), pr.getStock(), pr.getWeight()))
                .toList();
    }


}
