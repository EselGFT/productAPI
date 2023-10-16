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


public class ProductTest {

    Category category;
    Product product;
    Validator validator;

    @BeforeEach
    public void setUp(){
        this.category = new Category("Electronics", 25.0);
        this.product = new Product("Laptop", "Powerful laptop", category, 999.99, 10, 1.0);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();   
    }

    @Test
    @DisplayName("When a product is created, Then values should be set correctly")
    public void productConstructorTest() {
        
        assertEquals("Laptop", product.getName());
        assertEquals("Powerful laptop", product.getDescription());
        assertEquals(category, product.getCategory());
        assertEquals(999.99, product.getPrice()); 
        assertEquals(10, product.getStock());
    
    }

    @Test
    @DisplayName("When a product is created with the default constructor, Then default values should be set")
    public void productDefaultConstructorTest() {

        Product productDefault = new Product();

        assertNull(productDefault.getName());
        assertNull(productDefault.getDescription());
        assertNull(productDefault.getCategory());
        assertNull(productDefault.getPrice());
        assertEquals(0, productDefault.getStock());
    }

    @Test
    @DisplayName("Getting product name should return the correct name")
    public void getNameTest() {
        assertEquals("Laptop", product.getName());
    }

    @Test
    @DisplayName("Getting product description should return the correct description")
    public void getDescriptionTest() {
        assertEquals("Powerful laptop", product.getDescription());
    }

    @Test
    @DisplayName("Getting product category should return the correct category")
    public void getCategoryTest() {
        assertEquals(category, product.getCategory());
    }

    @Test
    @DisplayName("Getting product price should return the correct price")
    public void getPriceTest() {
        assertEquals(999.99, product.getPrice(), 0.01);
    }

    @Test
    @DisplayName("Getting product stock should return the correct stock")
    public void getStockTest() {
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Getting product stock should return the correct stock")
    public void getWeightTest() {
        assertEquals(1.0, product.getWeight());
    }    

    @Test
    @DisplayName("Setting a new product description should update the description")
    public void setDescriptionTest() {
        product.setDescription("New Product Description");
        assertEquals("New Product Description", product.getDescription());
    }

    @Test
    @DisplayName("Setting a new product category should update the category")
    public void setCategoryTest() {
        Category newCategory = new Category("food", 25.0);
        product.setCategory(newCategory);
        assertEquals(newCategory, product.getCategory());
    }

    @Test
    @DisplayName("Setting a new product price should update the price")
    public void setPriceTest() {
        product.setPrice(20.0);
        assertEquals(20.0, product.getPrice());
    }

    @Test
    @DisplayName("Setting a new product stock should update the stock")
    public void setStockTest() {
        product.setStock(200);
        assertEquals(200, product.getStock());
    }

    @Test
    @DisplayName("Comparing two products with equals method should return true")
    public void equalsTest() {
        Product product2 = new Product("Laptop", "Powerful laptop", category, 999.99, 10,1.0);
        assertEquals(product, product2);
    }

    @Test
    @DisplayName("Calculating hashCode for two equal products should return the same hash code")
    public void hashCodeTest() {
        Product product2 = new Product("Laptop", "Powerful laptop", category, 999.99, 10,1.0);
        assertEquals(product.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Converting a product to a string should not return null")
    public void toStringTest() {
        assertNotNull(product.toString());
    }
    
    @Test
    public void noErrorValidatorTest() {
        validator.validate(product);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(0, violations.size());        
    }
    
    @Test
    public void nameValidatorTest() {
        product.setName("");
        validator.validate(product);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());        
    }

    @Test
    public void priceValidatorTest() {
        product.setPrice(-1.0);
        validator.validate(product);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());        
    }

    @Test
    public void stockValidatorTest() {
        product.setStock(-1);
        validator.validate(product);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());        
    }

    @Test
    public void weightValidatorTest() {
        product.setWeight(-1.0);
        validator.validate(product);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());        
    }

    


    
}
