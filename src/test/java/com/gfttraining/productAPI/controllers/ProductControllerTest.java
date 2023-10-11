package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

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

import java.util.Arrays;
import java.util.List;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController productController;

    private Validator validator;
         
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         validator = factory.getValidator();
        
    }

    @Test
    public void postControllerTest(){
        
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;    
        Double productWeight = 1.0;

        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock, productWeight);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        Mockito.when(productService.createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight)).thenReturn(product);
        
        ResponseEntity<Product> response = productController.postMapping(productRequest);

        verify(productService, times(1)).createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        
        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());

    }

    @Test
    public void listProductsControllerTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23,1.0);
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13,1.1);

        List<Product> products = Arrays.asList(apple, dictionary);

        Mockito.when(productService.listProducts()).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.listProducts();

        verify(productService, times(1)).listProducts();

        assertEquals(products, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    public void postProductFailedControllerTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = -1.0;
        int productStock = 50;    
        Double productWeight = 1.0;

        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock, productWeight);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        Mockito.when(productService.createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight)).thenReturn(product);
        
        ResponseEntity<Product> response = productController.postMapping(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        
        verify(productService, times(1)).createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        
        assertEquals(1, violations.size());
        //assertThrows(ValidationException.class, () -> productController.postMapping(productRequest));

    }
}
