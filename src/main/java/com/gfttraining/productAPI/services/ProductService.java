package com.gfttraining.productAPI.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;

import jakarta.validation.Valid;

@Service
public class ProductService {
    
    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository){
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;

    }

    public Product createProduct(ProductRequest productRequest) {

        Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());
        
        Product product = new Product(productRequest.getName(), productRequest.getDescription(), category, productRequest.getPrice(), productRequest.getStock(), productRequest.getWeight());
        
        return productRepository.save(product);
        
    }
    
    public Product updateProduct (Long id, ProductRequest productRequest){
        
    	    	 
    	
    	
    	Product productUpdate = productRepository.findById(id).get();
        
    	if (productRequest.getName() != null) {
    		productUpdate.setName(productRequest.getName());    		
    	} 
    	if (productRequest.getDescription()!= null) {
    		productUpdate.setDescription(productRequest.getDescription());    		
    	}
    	if (productRequest.getCategory() != null) {
            Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());
    		productUpdate.setCategory(category);    		
    	}
    	if (productRequest.getPrice() != null) {
    		productUpdate.setPrice(productRequest.getPrice());    		
    	}
    	if(productRequest.getStock() != null){
    		productUpdate.setStock(productRequest.getStock());    		
    	}
        if (productRequest.getWeight() != null) {
            productUpdate.setWeight(productRequest.getWeight());
        }
    	
    	return productRepository.save(productUpdate);
    	
    	
    	
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(productRequest -> createProduct(productRequest))
                .toList();
    }

    public List<Product> listProductsWithIDs(List<Long> ids) {
        return productRepository.findAllById(ids);
    }


}
