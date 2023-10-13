package com.gfttraining.productAPI.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;

@RestController
public class ProductController {

    
    private final ProductService productService;   

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<Product> postMapping(@RequestBody ProductRequest productRequest){
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>( 
                productService.createProduct(
                    productRequest.getName(),
                    productRequest.getDescription(),
                    productRequest.getCategory(),
                    productRequest.getPrice(),
                    productRequest.getStock()),
                headers,   
                HttpStatus.OK
            );        
    }
    
    @PutMapping("/products/{id_product}") 
    public ResponseEntity<Product> putUpdate (@PathVariable int id_product, @RequestBody ProductRequest UpdateProductRequest) {
    	HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>( 
                productService.updateProduct(
                		id_product,
                		UpdateProductRequest.getName(),
                		UpdateProductRequest.getDescription(),
                		UpdateProductRequest.getCategory(),
                		UpdateProductRequest.getPrice(),
                		UpdateProductRequest.getStock()),
                headers,   
                HttpStatus.OK
            );     
    }
    
    /*
    @GetMapping("/products")
    public List<Product> listaProductos() {
		
		return productService.products();
	}
    
    @GetMapping("/products/{id}")
    public Product productById(int id) {
		
		return productService.productById(id);
	}*/

}
