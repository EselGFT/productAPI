package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import com.gfttraining.productAPI.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("GIVEN a product's information WHEN the createProduct method from the product controller is called THEN a product is created with the provided information")
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
        
        ResponseEntity<Product> response = productController.createProduct(productRequest);

        verify(productService, times(1)).createProduct(productRequest);
        
        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());

    }

    @Test
    @DisplayName("GIVEN a product's information WHEN the product's controller updateProduct method is called THEN the provided product's information is updated with te new information")
    public void putUpdateControllerTest() throws NonExistingProductException, InvalidCartConnectionException, InvalidCartResponseException {

    	String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;
        Double productWeight = 1.0;
        long  id = 1;

        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock,productWeight);
        Product product = new Product(productRequest, new Category("other",0.0));

        Mockito.when(productService.updateProduct(id,productRequest)).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(id, productRequest);

        verify(productService, times(1)).updateProduct(id,productRequest);

        assertEquals(product, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    @DisplayName("GIVEN an existing product ID WHEN the deleteProduct method is executed THEN the product is deleted")
    public void deleteProductsControllerTest () throws NonExistingProductException {
        long id = 1;

        Mockito.doNothing().when(productService).deleteProduct(id);

        ResponseEntity<?> response = productController.deleteProduct(id);

        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    @DisplayName("GIVEN a non existent product ID WHEN the deleteProduct method is executed THEN an exception is thrown")
    public void deleteProductsExceptionControllerTest () throws NonExistingProductException {
        int id = 100;

        Mockito.doThrow(NonExistingProductException.class).when(productService).deleteProduct(id);

        assertThrows(NonExistingProductException.class, () -> productController.deleteProduct(id));
    }

    @Test
    @DisplayName("WHEN the product's controller listProducts method is called THEN a list containing all the products in the database is returned")
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
    
    // start of searchProducts() tests

    @Test
    @DisplayName("GIVEN two products that contain ap WHEN the method is called with that input THEN a list containing both is returned")
    void searchMultipleProductsTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);
        Product apartment = new Product("Apartment", "A penthouse", new Category("other", 0.0), 12000.0, 1, 1000.1);

        List<Product> fullList = List.of(apple, apartment);

        Mockito.when(productService.listProductsByNameContainsIgnoreCase("ap")).thenReturn(fullList);

        ResponseEntity<List<Product>> apQuery = productController.searchProducts("ap");

        verify(productService, times(1)).listProductsByNameContainsIgnoreCase("ap");

        assertEquals(fullList, apQuery.getBody());
    }

    @Test
    void searchOneProductTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);

        List<Product> appleList = List.of(apple);

        Mockito.when(productService.listProductsByNameContainsIgnoreCase("APPLE")).thenReturn(appleList);

        ResponseEntity<List<Product>> appleQuery = productController.searchProducts("APPLE");

        verify(productService, times(1)).listProductsByNameContainsIgnoreCase("APPLE");

        assertEquals(appleList, appleQuery.getBody());
    }

    @Test
    void noResultsProductSearchTest() {
        List<Product> emptyList = List.of();

        Mockito.when(productService.listProductsByNameContainsIgnoreCase("butter")).thenReturn(emptyList);

        ResponseEntity<List<Product>> butterQuery = productController.searchProducts("butter");

        verify(productService, times(1)).listProductsByNameContainsIgnoreCase("butter");

        assertEquals(emptyList, butterQuery.getBody());
    }

    // end of searchProducts() tests

    @Test
    @DisplayName("GIVEN a product's information that doesn't follow the requirements WHEN creating a product through the controller THEN 1 violation should emerge")
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

        ResponseEntity<Product> response = productController.createProduct(productRequest);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);

        verify(productService, times(1)).createProduct(productRequest);

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("GIVEN a list of products WHEN the controller's bulkCreateProducts method is called THEN all products from the list are created")
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

        ResponseEntity<List<Product>> response = productController.bulkCreateProducts(productRequests);

        assertEquals(products, response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void getProductsWithIDsTest() throws NonExistingProductException{
        List<ProductDTO> productDTOs = Arrays.asList(

            new ProductDTO(0,BigDecimal.valueOf(10.0),50,1.0),
            new ProductDTO(0,BigDecimal.valueOf(10.0),50,1.0)
        );

        List<Long> idList = Arrays.asList(1L, 2L);
        Mockito.when(productService.createProductResponsesWithProductIDs(idList)).thenReturn(productDTOs);

        ResponseEntity<List<ProductDTO>> retrievedProducts = productController.getProductsBasicInfo(idList);

        assertEquals(productDTOs, retrievedProducts.getBody());
    }

    @Test
    public void ProductsToSubmitTest() throws NonExistingProductException, NotEnoughStockException {
        List<ProductToSubmit> productsToSubmit = Arrays.asList(
                new ProductToSubmit(1L,5),
                new ProductToSubmit(2L,5)
        );

        List<ProductDTO> productDTOs = Arrays.asList(

                new ProductDTO(1,BigDecimal.valueOf(10.0),45,1.0),
                new ProductDTO(2,BigDecimal.valueOf(10.0),45,1.0)
        );

        Mockito.when(productService.checkIfEnoughStockAndSubtract(productsToSubmit)).thenReturn(productDTOs);


        ResponseEntity<List<ProductDTO>> retrievedProducts = productController.reduceStock(productsToSubmit);

        assertEquals(productDTOs, retrievedProducts.getBody());
    }

}
