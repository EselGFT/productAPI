package com.gfttraining.productAPI.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ProductRequestTest {

    ProductRequest productRequest;
    Validator validator;
    
    @BeforeEach
    public void setUp(){
        this.productRequest = new ProductRequest("Laptop", "Powerful laptop", "laptop", 999.99, 10,1.0);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    @DisplayName("When a product request is created, Then values should be set correctly")
    public void productRequestConstructorTest() {

        assertEquals("Laptop", productRequest.getName());
        assertEquals("Powerful laptop", productRequest.getDescription());
        assertEquals("laptop", productRequest.getCategory());
        assertEquals(999.99, productRequest.getPrice()); 
        assertEquals(10, productRequest.getStock());
        assertEquals(1.0 ,productRequest.getWeight());
    }

    @Test
    @DisplayName("When a product request is created with the default constructor, Then default values should be set")
    public void productRequestDefaultConstructorTest() {

        ProductRequest productRequestDefault = new ProductRequest();

        assertNull(productRequestDefault.getName());
        assertNull(productRequestDefault.getDescription());
        assertNull(productRequestDefault.getCategory());
        assertNull(productRequestDefault.getPrice());
        assertNull(productRequestDefault.getStock());
    }

    @Test
    @DisplayName("Getting product name should return the correct name")
    public void getNameTest() {
        assertEquals("Laptop", productRequest.getName());
    }

    @Test
    @DisplayName("Getting product description should return the correct description")
    public void getDescriptionTest() {
        assertEquals("Powerful laptop", productRequest.getDescription());
    }

    @Test
    @DisplayName("Getting product category should return the correct category")
    public void getCategoryTest() {
        assertEquals("laptop", productRequest.getCategory());
    }

    @Test
    @DisplayName("Getting product price should return the correct price")
    public void getPriceTest() {
        assertEquals(999.99, productRequest.getPrice(), 0.01);
    }

    @Test
    @DisplayName("Getting product stock should return the correct stock")
    public void getStockTest() {
        assertEquals(10, productRequest.getStock());
    }

    @Test
    @DisplayName("Getting product stock should return the correct stock")
    public void getWeightTest() {
        assertEquals(1.0, productRequest.getWeight());
    }    

    @Test
    @DisplayName("Setting a new product description should update the description")
    public void setDescriptionTest() {
        productRequest.setDescription("New Product Description");
        assertEquals("New Product Description", productRequest.getDescription());
    }

    @Test
    @DisplayName("Setting a new product category should update the category")
    public void setCategoryTest() {
        
        productRequest.setCategory("food");
        assertEquals("food", productRequest.getCategory());
    }

    @Test
    @DisplayName("Setting a new product price should update the price")
    public void setPriceTest() {
        productRequest.setPrice(20.0);
        assertEquals(20.0, productRequest.getPrice());
    }

    @Test
    @DisplayName("Setting a new product stock should update the stock")
    public void setStockTest() {
        productRequest.setStock(200);
        assertEquals(200, productRequest.getStock());
    }

    @Test
    @DisplayName("Comparing two products with equals method should return true")
    public void equalsTest() {
        ProductRequest productRequest2 = new ProductRequest("Laptop", "Powerful laptop", "laptop", 999.99, 10,1.0);
        assertEquals(productRequest, productRequest2);
    }

    @Test
    @DisplayName("Calculating hashCode for two equal products should return the same hash code")
    public void hashCodeTest() {
        ProductRequest productRequest2 = new ProductRequest("Laptop", "Powerful laptop", "laptop", 999.99, 10,1.0);
        assertEquals(productRequest.hashCode(), productRequest2.hashCode());
    }

    @Test
    @DisplayName("Converting a product to a string should not return null")
    public void toStringTest() {
        assertNotNull(productRequest.toString());
    }

    @Test
    @DisplayName("GIVEN an empty name WHEN its validated THEN it creates a violation")
    public void nameNotBlankValidatorTest() {
        productRequest.setName("");
        validator.validate(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        
    }   

    
    @Test
    @DisplayName("GIVEN a negative price WHEN its validated THEN it creates a violation")
    public void priceValidatorTest() {
        productRequest.setPrice(-1.0);
        validator.validate(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        
    }  
    
    @Test
    @DisplayName("GIVEN a negative stock WHEN its validated THEN it creates a violation")
    public void stockValidatorTest() {
        productRequest.setStock(-1);
        validator.validate(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        
    }  

    @Test
    @DisplayName("GIVEN a negative stock WHEN its validated THEN it creates a violation")
    public void weightValidatorTest() {
        productRequest.setStock(-1);
        validator.validate(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        
    }      
    

}
