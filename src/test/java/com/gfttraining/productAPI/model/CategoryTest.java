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

public class CategoryTest {
    
    Category category;
    Validator validator;

    @BeforeEach
    public void setUp(){
        this.category = new Category("Electronics", 25.0);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    @DisplayName("When a category is created, Then values should be set correctly")
    public void categoryConstructorTest(){

        assertEquals("Electronics", category.getName());
        assertEquals(25.0, category.getDiscount());
    }
    
    @Test
    @DisplayName("When a category is created with the default constructor, Then default values should be set")
    public void categoryDefaultConstructorTest(){
        Category categoryDefault = new Category();

        assertNull(categoryDefault.getName());
        assertEquals(0.0, categoryDefault.getDiscount());
    }

    @Test
    @DisplayName("Getting category name should return the correct name")
    public void getNameTest() {
        assertEquals("Electronics", category.getName());
    }

    @Test
    @DisplayName("Getting category discount should return the correct discount")
    public void getDiscountTest() {
        assertEquals(25.0, category.getDiscount(), 0.01);
    }

    @Test
    @DisplayName("Setting a new category name should update the name")
    public void setNameTest() {
        category.setName("Food");
        assertEquals("Food", category.getName());
    }

    @Test
    @DisplayName("Setting a new category discount should update the discount")
    public void setDiscountTest() {
        category.setDiscount(20.0);
        assertEquals(20.0, category.getDiscount(), 0.01);
    }

    @Test
    @DisplayName("Comparing two categories with equals method should return true")
    public void equalsTest() {
        Category category2 = new Category("Electronics", 25.0);
        assertEquals(category, category2);
    }

    @Test
    @DisplayName("Calculating hashCode for two equal categories should return the same hash code")
    public void hashCodeTest() {
        Category category2 = new Category("Electronics", 25.0);
        assertEquals(category.hashCode(), category2.hashCode());
    }

    @Test
    @DisplayName("Converting a category to a string should not return null")
    public void toStringTest() {
        assertNotNull(category.toString());
    }
    
    @Test
    @DisplayName("GIVEN an empty category name WHEN its validated THEN it creates a violation")
    public void nameNotBlankValidatorTest() {
        category.setName("");
        validator.validate(category);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        
    }    

    @Test
    @DisplayName("GIVEN a negative discount WHEN its validated THEN it creates a violation")
    public void negativeDiscountValidatorTest() {
        category.setDiscount(-1.0);
        validator.validate(category);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        
    }
    
    @Test
    @DisplayName("GIVEN a discount over 100 WHEN its validated THEN it creates a violation")
    public void overHundredDiscountValidatorTest() {
        category.setDiscount(101.0);
        validator.validate(category);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        
    }   

}
