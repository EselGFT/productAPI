package com.gfttraining.productAPI.services;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public Product createProduct(ProductRequest productRequest) {

        Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());
        
        Product product = new Product(productRequest.getName(), productRequest.getDescription(), category, productRequest.getPrice(), productRequest.getStock(), productRequest.getWeight());
        
        return productRepository.save(product);
        
    }
    
    public Product updateProduct (int id, ProductRequest productRequest){
        System.out.println(productRequest);

        Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());
    	Product productUpdate = productRepository.findById(id).get();

        productUpdate.setName(productRequest.getName());
        productUpdate.setDescription(productRequest.getDescription());
    	productUpdate.setCategory(category);
    	productUpdate.setPrice(productRequest.getPrice());
    	productUpdate.setStock(productRequest.getStock());
    	productUpdate.setWeight(productRequest.getWeight());

    	
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


}
