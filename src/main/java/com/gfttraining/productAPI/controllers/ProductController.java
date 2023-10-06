package com.gfttraining.productAPI.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

import jakarta.servlet.http.HttpServletRequest;



@RestController
public class ProductController {

    @Autowired
    private ProductService productService;   

    // @PostMapping("product")
    // public int postProduct(@RequestBody Product product ){
    //     return productService.createProduct()
    // }

    // @PostMapping("/product1")
    // public long posttMapping(HttpServletRequest request){
        
    //     return productService.createProduct(
    //         request.getParameter("name"), 
    //         request.getParameter("description"), 
    //         request.getParameter("category"), 
    //         Double.parseDouble(request.getParameter("price")), 
    //         Integer.parseInt(request.getParameter("stock")));
        
    // }

    @PostMapping("/product2")
    public ResponseEntity<Product> postMapping(@RequestBody ProductRequest productRequest){
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>( 
                productService.createProduct(productRequest.getName(),productRequest.getDescription(),productRequest.getCategory(),productRequest.getPrice(),productRequest.getStock()),
                headers,   
                HttpStatus.OK);
            

        
    }

}
