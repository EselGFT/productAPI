package com.gfttraining.productAPI.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gfttraining.productAPI.exceptions.NotAllProductsFoundException;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductDTO;
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

        Category testCategory = new Category("TestCategory", 30.0);// poridamos porner aqui category name no?
        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, testCategory, productPrice, productStock, productWeight);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);

        Mockito.when(categoryRepository.findById(categoryName)).thenReturn(Optional.of(testCategory));
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productRequest);
        
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
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);

        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productRequest);
        
        verify(categoryRepository, times(1)).findById(categoryName);
        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(other, createdProduct.getCategory());
    }
    
    @Test
    @DisplayName("GIVEN a product's updated information WHEN the original its updated THEN the updated product's information should match the given")
    void updateProductsTest () throws NonExistingProductException {
    	
    	
    	String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;
        Double productWeight = 1.0;
        long id = 1;
        
        Category other = new Category("other", 0.0);
        Product product = new Product(productName, productDescription, other, productPrice, productStock,productWeight);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        Mockito.when(categoryRepository.findById("other")).thenReturn(Optional.of(other));
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(product)).thenReturn(product);       
              
        Product productAfterUpdate = productService.updateProduct(id, productRequest);

        verify(categoryRepository, times(1)).findById("other");
        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(productAfterUpdate.getName(),productName);
        assertEquals(productAfterUpdate.getCategory().getName(), "other");
        assertEquals(productAfterUpdate.getDescription(),productDescription);
        assertEquals(productAfterUpdate.getPrice(), productPrice);
        assertEquals(productAfterUpdate.getStock(),productStock);
        assertEquals(productAfterUpdate.getWeight(), productWeight);
    }

    @Test
    @DisplayName("WHEN deletepProduct is executed THEN delete a product object")
    void deleteProductTest () throws NonExistingProductException {
        long id = 1;
        Category other = new Category("other", 0.0);
        Product product = new Product("TestProduct", "TestDescription", other, 10.0, 50,1.0);

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productRepository).deleteById(id);
        productService.deleteProduct(id);
        verify(productRepository,times(1)).deleteById(id);


    }

    @Test
    @DisplayName("WHEN Non Existing Products is try to delete  THEN NonExistingProductException is thrown")
    void deleteProductTrhowsExceptionTest () throws NonExistingProductException {
        long id = 1;
        Category other = new Category("other", 0.0);
        Product product = new Product("TestProduct", "TestDescription", other, 10.0, 50,1.0);

        //Mockito.when(productRepository.findById(1)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productRepository).deleteById(id);
        assertThrows(NonExistingProductException.class, () -> productService.deleteProduct(id));
        verify(productRepository,times(0)).deleteById(id);


    }

    // start of listProductById() tests

    @Test
    @DisplayName("WHEN a product is requested GIVEN its ID THEN the requested product is returned")
    void listProductTest() throws NonExistingProductException {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);
        long id = 1;
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(apple));

        Product found = productService.listProductById(1);

        verify(productRepository, times(1)).findById(id);

        assertEquals(apple,found);
        assertEquals(apple.getName(),found.getName());
        assertEquals(apple.getCategory().getName(), found.getCategory().getName());
        assertEquals(apple.getDescription(),found.getDescription());
        assertEquals(apple.getPrice(), found.getPrice());
        assertEquals(apple.getStock(),found.getStock());
        assertEquals(apple.getWeight(), found.getWeight());
    }

    // end of listProductById() tests

    // start of listProductsByNameContainsIgnoreCase() tests

    @Test
    @DisplayName("GIVEN two products that contain ap WHEN the method is called with that input THEN a list containing both is returned")
    void ListProductsByNameIgnoreCaseMultipleProductsTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);
        Product apartment = new Product("Apartment", "A penthouse", new Category("other", 0.0), 12000.0, 1, 1000.1);

        List<Product> fullList = List.of(apple, apartment);

        Mockito.when(productRepository.findByNameIgnoreCaseContaining("ap")).thenReturn(fullList);

        List<Product> apQuery = productService.listProductsByNameContainsIgnoreCase("ap");

        verify(productRepository, times(1)).findByNameIgnoreCaseContaining("ap");

        assertEquals(apQuery, fullList);
    }

    @Test
    @DisplayName("GIVEN a product with a camel case name WHEN the method is called with the name in caps THEN a list containing the product is returned")
    void ListProductsByNameIgnoreCaseUpperCaseTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);

        List<Product> appleList = List.of(apple);

        Mockito.when(productRepository.findByNameIgnoreCaseContaining("APPLE")).thenReturn(appleList);

        List<Product> appleQuery = productService.listProductsByNameContainsIgnoreCase("APPLE");

        verify(productRepository, times(1)).findByNameIgnoreCaseContaining("APPLE");

        assertEquals(appleQuery, appleList);
    }

    @Test
    @DisplayName("GIVEN an input that doesn't correspond with any product WHEN the method is called with that input THEN an empty list is returned")
    void ListProductsByNameIgnoreCaseNonExistentTest() {
        List<Product> emptyList = List.of();

        Mockito.when(productRepository.findByNameIgnoreCaseContaining("Butter")).thenReturn(emptyList);

        List<Product> butterQuery = productService.listProductsByNameContainsIgnoreCase("Butter");

        verify(productRepository, times(1)).findByNameIgnoreCaseContaining("Butter");

        assertEquals(butterQuery, emptyList);
    }

    // end of listProductsByNameContainsIgnoreCase() tests

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

        assertEquals(products, createdProducts);
    }

    
    @Test
    public void createProductsResponsesTest() {
        Category food = new Category("food", 25.0);
        List<Product> products = Arrays.asList(new Product(
                "TestProduct1", 
                "TestDescription1", 
                food, 
                10.0, 
                50,
                1.0));    
                
        BigDecimal bd = new BigDecimal(7.50);
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);        
        List<ProductDTO> productDTOS = productService.buildProductsDTOs(products);
        List<ProductDTO> expectedProductsResponses = Arrays.asList(new ProductDTO(0, roundedPrice, 50,1.0 ));
        assertEquals(expectedProductsResponses, productDTOS);
    }

    @Test
    public void listProductsWithIDsTest() throws NotAllProductsFoundException{

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
                food, 
                10.0, 
                100,
                1.0);        
        
        
        List<Product> products = Arrays.asList(productTest1, productTest2);
        List<Long> idList = Arrays.asList(Long.valueOf(1),Long.valueOf(2));

        Mockito.when(productRepository.findAllById(idList)).thenReturn(products);
        List<Product> productsExpected= Arrays.asList(productTest1, productTest2);
        List<Product> productsRetrievedList = productService.getProductsWithIDs(idList);
        assertEquals(productsExpected, productsRetrievedList);

    }

    @Test
    @DisplayName("GIVEN a list of products WHEN the getNumberOfProducts method is called THEN an int representing the ammount of products in the DB is returned")
    public void getNumberOfProductsTest() {

        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13 ,1.0);

        List<Product> products = Arrays.asList(apple, dictionary);

        Mockito.when(productRepository.findAll()).thenReturn(products);

        int numberOfProducts = productService.getNumberOfProducts();

        verify(productRepository, times(1)).findAll();

        assertEquals(2, numberOfProducts);
    }

    @Test
    @DisplayName("GIVEN a product WHEN the discounted price is calculated THEN the precise amount should be returned")
    public void calculateDiscountedPriceTest() {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23, 1.1);

        BigDecimal discountedPrice = productService.calculateDiscountedPrice(apple);

        assertEquals(new BigDecimal("0.94"), discountedPrice);
    }


}
