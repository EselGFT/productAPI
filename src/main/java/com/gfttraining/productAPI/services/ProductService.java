package com.gfttraining.productAPI.services;

import java.util.List;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.exceptions.NotAllProductsFoundException;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductResponse;
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
        
    public Product updateProduct (Long id, ProductRequest productRequest){
        
    	    	 
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

    public void deleteProduct (long id) throws NonExistingProductException {

        if (productRepository.findById(id).isEmpty()){
           throw new NonExistingProductException("The provided ID is non existent");
         }else {
            productRepository.deleteById(id);
         }


    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }



    public Product listProductById(long id) throws NonExistingProductException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NonExistingProductException("The provided ID is non existent"));
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(productRequest -> createProduct(productRequest))
                .toList();
    }

    public List<ProductResponse> listProductsWithIDs(List<Long> ids) throws NotAllProductsFoundException {
        List<Product> foundIds= productRepository.findAllById(ids);
        if(foundIds.size() == ids.size()){
            return foundIds.stream().map(product -> new ProductResponse(product)).toList();
        }else{           
            List<Long> notFoundIds = ids.stream()
                .filter(id -> foundIds.stream().noneMatch(product -> product.getId() == id))
                .toList();

            throw new NotAllProductsFoundException("Product IDs not found: " + notFoundIds);          
        }
    }

    public int getNumberOfProducts() {
        return productRepository.findAll().size();
    }
}
