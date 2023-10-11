package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void loadProductsControllerTest(){
        List<ProductRequest> productRequests = Arrays.asList(
            new ProductRequest(
                "TestProduct1", 
                "TestDescription1", 
                "TestCategory", 
                10.0, 
                50),
            new ProductRequest(
                "TestProduct2", 
                "TestDescription2", 
                "TestCategory", 
                10.0, 
                100)            
        );

        Category category =  new Category("other", 0.0);

        List<Product> products = Arrays.asList(
            new Product(
                "TestProduct1", 
                "TestDescription1", 
                category, 
                10.0, 
                50),
            new Product(
                "TestProduct2", 
                "TestDescription2", 
                category, 
                10.0, 
                100)  
        );

        Mockito.when(productService.createProducts(productRequests)).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.postLoadProducts(productRequests);

        assertEquals(products, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());


    }
}
