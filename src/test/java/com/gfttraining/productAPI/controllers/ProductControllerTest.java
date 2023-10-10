package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

public class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController productController;

         
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void postControllerTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;    

        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock);
        Mockito.when(productService.createProduct(productName, productDescription, categoryName, productPrice, productStock)).thenReturn(product);
        
        ResponseEntity<Product> response = productController.postMapping(productRequest);

        verify(productService, times(1)).createProduct(productName, productDescription, categoryName, productPrice, productStock);
        
        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());

    }
}
