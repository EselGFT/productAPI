package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.gfttraining.productAPI.model.Product;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductControllerIT {

    @LocalServerPort
    private int port;
    private WebTestClient client;
    @Autowired
    private ProductService productService;

    @PostConstruct
    void init() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }


    //////////////// ORDER STARTS HERE *****************************************************************
    @Test
    @DisplayName("1 Given a list of productRquests When a post is made to /products Then it should be saved in database and return the saved products")
    @Order(1)
    void postLoadProductsTest() {
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

        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequests)
                .exchange()

                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("TestProduct1")
                .jsonPath("$[0].description").isEqualTo("TestDescription1")
                .jsonPath("$[0].category.name").isEqualTo("other")
                .jsonPath("$[0].category.discount").isEqualTo(0.0)
                .jsonPath("$[0].price").isEqualTo(10.0)
                .jsonPath("$[0].stock").isEqualTo(50)
                .jsonPath("$[0].weight").isEqualTo(1.0)
                .jsonPath("$[1].name").isEqualTo("TestProduct2")
                .jsonPath("$[1].description").isEqualTo("TestDescription2")
                .jsonPath("$[1].category.name").isEqualTo("other")
                .jsonPath("$[1].category.discount").isEqualTo(0.0)
                .jsonPath("$[1].price").isEqualTo(10.0)
                .jsonPath("$[1].stock").isEqualTo(100)
                .jsonPath("$[1].weight").isEqualTo(1.0);
    }
    //2 PRODUCTS HAVE BEEN CREATED

    @Test
    @DisplayName("2 Given a list of productRequests with bad content When a post is made to /products Then it should return an error message")
    @Order(2)
    void postLoadProductsErrorTest() {
        List<ProductRequest> productRequests = Arrays.asList(
                new ProductRequest(
                        "TestProduct1",
                        "TestDescription1",
                        "TestCategory",
                        null,
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

        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequests)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(message -> assertTrue(message.contains("Price should not be null")));
    }

    @Test
    @DisplayName("3 Given a productRequests  When a post is made to /product Then it should be saved in the database and return the saved product")
    @Order(3)
    void postLoadProductTest() {

        ProductRequest productRequest = new ProductRequest(
                "TestProduct1",
                "TestDescription1",
                "TestCategory",
                10.0,
                50,
                1.0);


        client.post().uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()

                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestProduct1")
                .jsonPath("$.description").isEqualTo("TestDescription1")
                .jsonPath("$.category.name").isEqualTo("other")
                .jsonPath("$.category.discount").isEqualTo(0.0)
                .jsonPath("$.price").isEqualTo(10.0)
                .jsonPath("$.stock").isEqualTo(50)
                .jsonPath("$.weight").isEqualTo(1.0);


    }

    // 1 PRODUCT HAS BEEN CREATED - TOTAL: 5
    @Test
    @DisplayName("4 Given a productRequest with bad content When a post is made to /product Then it should return an error message")
    @Order(4)
    void postLoadProductErrorTest() {

        ProductRequest productRequest = new ProductRequest(
                "TestProduct1",
                "TestDescription1",
                "TestCategory",
                null,
                50,
                1.0);


        client.post().uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(message -> assertTrue(message.contains("Price should not be null")));

    }

    @Test
    @Order(5)
    @DisplayName("5")
    void listAllTest() {

        int numberOfProducts = productService.getNumberOfProducts();
        //Apple
        productService.createProduct(new ProductRequest("Apple", "A rounded food object", "food", 1.25, 23, 1.0));

        //Dictionary
        productService.createProduct(new ProductRequest("Dictionary", "A book that defines words", "books", 19.89, 13, 1.0));

        client.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[" + numberOfProducts + "].name").isEqualTo("Apple")
                .jsonPath("$[" + numberOfProducts + "].description").isEqualTo("A rounded food object")
                .jsonPath("$[" + numberOfProducts + "].category.name").isEqualTo("food")
                .jsonPath("$[" + numberOfProducts + "].price").isEqualTo(1.25)
                .jsonPath("$[" + numberOfProducts + "].stock").isEqualTo(23)
                .jsonPath("$[" + numberOfProducts + "].weight").isEqualTo(1.0)
                .jsonPath("$[" + (numberOfProducts + 1) + "].name").isEqualTo("Dictionary")
                .jsonPath("$[" + (numberOfProducts + 1) + "].description").isEqualTo("A book that defines words")
                .jsonPath("$[" + (numberOfProducts + 1) + "].category.name").isEqualTo("books")
                .jsonPath("$[" + (numberOfProducts + 1) + "].price").isEqualTo(19.89)
                .jsonPath("$[" + (numberOfProducts + 1) + "].stock").isEqualTo(13)
                .jsonPath("$[" + (numberOfProducts + 1) + "].weight").isEqualTo(1.0);
    }
    //2 PRODUCTS HAVE BEEN CREATED total 8 products

    @Test
    @DisplayName("6")
    @Order(6)
    void listOneProductByIDTest() {
        //Apple
        productService.createProduct(new ProductRequest("Apple", "A rounded food object", "food", 1.25, 23, 1.0));

        int appleID = productService.getNumberOfProducts();// AQUI ESTO ES LO QUE FALLA

        client.get().uri("/products/" + appleID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Apple")
                .jsonPath("$.description").isEqualTo("A rounded food object")
                .jsonPath("$.category.name").isEqualTo("food")
                .jsonPath("$.price").isEqualTo(1.25)
                .jsonPath("$.stock").isEqualTo(23);
    }

    // no 1 PRODUCT HAS BEEN CREATED
    @Test
    @DisplayName("7")
    @Order(7)
    void listOneProductWithStringIDTest() {
        client.get().uri("/products/str")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$").isEqualTo("Wrong type exception, please consult the OpenAPI documentation");
    }

    @Test
    @DisplayName("8")
    @Order(8)
    void listOneNonExistentProductTest() {
        int numberOfProducts = productService.getNumberOfProducts();

        client.get().uri("/products/" + (numberOfProducts + 1))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("Product IDs not found: 10");
    }

    @Test
    @DisplayName("9 Given a list of IDs When a post is made to /productsWithIDs Then it should return the ProductDTO of the found IDs")
    @Order(9)
    void productsWithIDsTest() {
        long id1 = 1;
        long id2 = 2;
        List<Long> IDsList = Arrays.asList(id1, id2);

        client.post().uri("/productsWithIDs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IDsList)
                .exchange()

                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].price").isEqualTo(7.5)
                .jsonPath("$[0].stock").isEqualTo(10)
                .jsonPath("$[0].weight").isEqualTo(1.0)
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].price").isEqualTo(4.25)
                .jsonPath("$[1].stock").isEqualTo(20)
                .jsonPath("$[1].weight").isEqualTo(1.0);
    }

    @Test
    @DisplayName("10 Given a list of IDs When a post is made to /productsWithIDs Then it should return the ProductDTO of the found IDs")
    @Order(10)
    void productsWithIDsErrorTest() {
        long id1 = 1;
        long id2 = 2;
        long id4 = 100;
        List<Long> IDsList = Arrays.asList(id1, id2, id4);

        client.post().uri("/productsWithIDs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IDsList)
                .exchange()

                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(message -> assertEquals("Product IDs not found: [100]", message));
    }

    @Test
    @DisplayName("11 GIVEN a product's information WHEN the product's controller putUpdate method is called THEN the provided product's information is updated with te new information")
    @Order(11)
    void productUpdateIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.get().uri("/products/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product product = response.getResponseBody();
                    assertEquals("books", product.getCategory().getName());
                    assertEquals("Small book", product.getDescription());
                    assertEquals("Book", product.getName());
                    assertEquals(5.0, product.getPrice());
                    assertEquals(20, product.getStock());
                    assertEquals(1.0, product.getWeight());
                });

        client.put().uri("/products/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product afterUpdateProduct = response.getResponseBody();
                    assertEquals("TestProduct", afterUpdateProduct.getName());
                    assertEquals("", afterUpdateProduct.getDescription());
                    assertEquals("other", afterUpdateProduct.getCategory().getName());
                    assertEquals(10.0, afterUpdateProduct.getPrice());
                    assertEquals(50, afterUpdateProduct.getStock());
                    assertEquals(2.0, afterUpdateProduct.getWeight());

                });
    }

    @Test
    @DisplayName(" 12 GIVEN2 a product's information WHEN the product's controller putUpdate method is called THEN the provided product's information is updated with te new information")
    @Order(12)
    void productUpdateThrowExceptionIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.put().uri("/products/" + productService.getNumberOfProducts() + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");


    }

    @Test
    @DisplayName("13 WHEN deleteProduct is executed THEN delete a product object")
    @Order(13)
    void productDeleteIT() {

        int numberOfProducts = productService.getNumberOfProducts();


        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(numberOfProducts);

        client.delete().uri("/products/" + numberOfProducts)
                .exchange()
                .expectStatus().isOk() // en el udemy el le pone un no content porque es lo que entiendo tiene el configurado
                .expectBody().isEmpty();

        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(numberOfProducts - 1);

        client.get().uri("/products/" + numberOfProducts).exchange()
                .expectStatus().isNotFound();
        //.expectBody().isEmpty()

    }

    @Test
    @DisplayName("14 GIVEN a product's information WHEN the product's controller putUpdate method is called with incorrect Id THEN throws the NonExistingProductException exception")
    @Order(14)
    void productDeleteThrowExceptionIT() {
        //GIVEN

        client.delete().uri("/products/" + (productService.getNumberOfProducts() + 1))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");
        ;
    }


    //////////////////////////////////////// ORDER ENDS HERE ///////////////////////////////////////////
}