package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;


public class ProductServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(categoryRepository, productRepository);
    }

    @Test
    @DisplayName("When a product is created, Then it should be associated with the correct category")
    void createProductTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;
        Double productWeight = 1.0;

        Category testCategory = new Category("TestCategory", 30.0);
        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, testCategory, productPrice, productStock, productWeight);
        
        Mockito.when(categoryRepository.findById(categoryName)).thenReturn(Optional.of(testCategory));
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        
        verify(categoryRepository, times(1)).findById(categoryName);
        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(testCategory, createdProduct.getCategory());
        
    }

    @Test
    @DisplayName("When a product is created but the category is not found, Then it should be associated with the 'other' category")
    void createOtherCategoryProductTest(){
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;
        Double productWeight = 1.0;

        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, other, productPrice, productStock, productWeight);
        
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        
        verify(categoryRepository, times(1)).findById(categoryName);
        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(other, createdProduct.getCategory());
    }

    @Test
    @DisplayName("When products are requested to be listed, a list that contains all of them is returned")
    void listProductsTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13 ,1.0);

        List<Product> products = Arrays.asList(apple, dictionary);

        Mockito.when(productRepository.findAll()).thenReturn(products);

        List<Product> productsToShow = productService.listProducts();

        assertNotNull(productsToShow);
        assertEquals(products.size(), productsToShow.size());
    }    
    
    @Test
    @DisplayName("When a product is created but the category is not foun, Then it should be associated with the 'other' category")
    void createProductsTest(){
        List<ProductRequest> productRequests = Arrays.asList(
            new ProductRequest(
                "TestProduct1", 
                "TestDescription1", 
                "food", 
                10.0, 
                50,
                1.0),
            new ProductRequest(
                "TestProduct2", 
                "TestDescription2", 
                "TestCategory", 
                10.0, 
                100,
                1.0)            
        );

        Category other = new Category("other", 0.0);
        Category food = new Category("food", 25.0);
 
        Product productTest1 = new Product(
                "TestProduct1", 
                "TestDescription1", 
                food, 
                10.0, 
                50,
                1.0);

        Product productTest2 = new Product(
                "TestProduct2", 
                "TestDescription2", 
                other, 
                10.0, 
                100,
                1.0);        
        
        
        List<Product> products = Arrays.asList(productTest1, productTest2);
        Mockito.when(categoryRepository.findById("food")).thenReturn(Optional.of(food));
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));

        Mockito.when(productRepository.save(productTest1)).thenReturn(productTest1);
        Mockito.when(productRepository.save(productTest2)).thenReturn(productTest2);

        List<Product> createdProducts = productService.createProducts(productRequests);
        
        //verify(categoryRepository, times(1)).findById(categoryName);
       // verify(categoryRepository, times(1)).findById("other");
      //  verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(products, createdProducts);
    }


}
