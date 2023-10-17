package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.Set;

import java.util.Arrays;
import java.util.List;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


public class ProductControllerTest {

    @Mock
    ProductService productService;
    @Mock
    ProductRepository productRepository;

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
        Mockito.when(productService.createProduct(productRequest)).thenReturn(product);
        
        ResponseEntity<Product> response = productController.postMapping(productRequest);

        verify(productService, times(1)).createProduct(productRequest);
        
        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());

    }

    @Test
    public void putUpdateControllerTest() {

    	String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;
        Double productWeight = 1.0;
        int id = 1;
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock,productWeight);
        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock,productWeight);
        Mockito.when(productService.updateProduct(id,productRequest)).thenReturn(product);
        ResponseEntity<Product> response = productController.putUpdate(id, productRequest);


        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());



    }

    @Test
    @DisplayName("WHEN deleteProduct is executed THEN delete a product object")
    public void deleteProductsControllerTest () throws NonExistingProductException {
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13,1.1);
        int id = 1;

        Mockito.when(productRepository.findById(1)).thenReturn(Optional.of(dictionary));
        Mockito.doNothing().when(productService).deleteProduct(id);
        ResponseEntity<?> response = productController.deleteProduct(id);
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());


    }

    @Test
    @DisplayName("WHEN deleteProduct is executed THEN delete a product object")
    public void deleteProductsExceptionControllerTest () throws NonExistingProductException {
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13,1.1);
        int id = 1;

        Mockito.doThrow(NonExistingProductException.class).when(productService).deleteProduct(id);

        assertThrows(NonExistingProductException.class, () -> productController.deleteProduct(id));


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

    // start of listProductById() tests

    @Test
    @DisplayName("WHEN requesting a product GIVEN it's ID THEN the product with the corresponding ID is returned")
    public void listProductControllerTest() throws NonExistingProductException {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23,1.0);

        Mockito.when(productService.listProductById(0)).thenReturn(apple);

        ResponseEntity<Product> response = productController.getProductById(0);

        verify(productService, times(1)).listProductById(0);

        assertEquals(apple, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    @DisplayName("WHEN requesting a product GIVEN a non existing product ID THEN an instance of NonExistingProductException is thrown")
    void nonExistingProductListControllerTest() throws NonExistingProductException{
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23,1.0);

        Mockito.when(productService.listProductById(1)).thenReturn(apple);
        Mockito.when(productService.listProductById(2)).thenThrow(new NonExistingProductException(""));

        assertDoesNotThrow(() -> productController.getProductById(1));

        assertThrows(NonExistingProductException.class, () -> productController.getProductById(2));
    }

    // end of listProductById() tests

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
        Mockito.when(productService.createProduct(productRequest)).thenReturn(product);

        ResponseEntity<Product> response = productController.postMapping(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);

        verify(productService, times(1)).createProduct(productRequest);

        assertEquals(1, violations.size());


    }

    @Test
    public void loadProductsControllerTest(){
        List<ProductRequest> productRequests = Arrays.asList(
            new ProductRequest(
                "TestProduct1",
                "TestDescription1",
                "TestCategory",
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

        Category category =  new Category("other", 0.0);

        List<Product> products = Arrays.asList(
            new Product(
                "TestProduct1",
                "TestDescription1",
                category,
                10.0,
                50,
                1.0),
            new Product(
                "TestProduct2",
                "TestDescription2",
                category,
                10.0,
                100,
                1.0)
        );

        Mockito.when(productService.createProducts(productRequests)).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.postLoadProducts(productRequests);

        assertEquals(products, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());


    }
}
