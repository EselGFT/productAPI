package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;


public class ProductServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("When a product is created, Then it should be associated with the correct category")
    void createProductTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;

        Category testCategory = new Category("TestCategory", 30.0);
        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, testCategory, productPrice, productStock);
        
        Mockito.when(categoryRepository.findById(categoryName)).thenReturn(Optional.of(testCategory));
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productName, productDescription, categoryName, productPrice, productStock);
        
        verify(categoryRepository, times(1)).findById(categoryName);
        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(testCategory, createdProduct.getCategory());
        
    }

    @Test
    @DisplayName("When a product is created but the category is not foun, Then it should be associated with the 'other' category")
    void createOtherCategoryProductTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;

        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, other, productPrice, productStock);
        
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productName, productDescription, categoryName, productPrice, productStock);
        
        verify(categoryRepository, times(1)).findById(categoryName);
        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(other, createdProduct.getCategory());
    }


}
