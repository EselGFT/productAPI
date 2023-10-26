package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.model.ProductToSubmit;
import com.gfttraining.productAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductControllerExceptionsIT {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @Autowired
    private ProductService productService;

    @PostConstruct
    void init() {                                           // Initiates the web client
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }

    @Test
    @DisplayName("Given a list of productRequests with a non valid one When a post is made to /products/bulkCreate Then it should return an error message")
    @Order(1)
    void createProductsBulkErrorTest() {
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

        client.post().uri("/products/bulkCreate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequests)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(message -> assertTrue(message.contains("Price should not be null")));
    }

    @Test
    @DisplayName("Given a productRequest with bad content When a post is made to /products Then it should return an error message")
    @Order(2)
    void createProductErrorTest() {

        ProductRequest productRequest = new ProductRequest(
                "TestProduct1",
                "TestDescription1",
                "TestCategory",
                null,
                50,
                1.0);


        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(message -> assertTrue(message.contains("Price should not be null")));

    }

    @Test
    @DisplayName("GIVEN a string product ID WHEN tying to list it THEN an error should be invoked")
    @Order(3)
    void listOneProductWithStringIDTest() {
        client.get().uri("/products/str")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$").isEqualTo("Wrong type exception, please consult the OpenAPI documentation");
    }

    @Test
    @DisplayName("GIVEN a non existing product ID WHEN trying to list it THEN an error should be found")
    @Order(4)
    void listOneNonExistentProductTest() {
        long nonExistingID = productService.getLastCreatedID() + 1;

        client.get().uri("/products/" + nonExistingID)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("Product IDs not found: " + nonExistingID);
    }

    @Test
    @DisplayName("Given a list of IDs When a post is made to /products/getBasicInfo Then it should return the ProductDTO of the found IDs")
    @Order(5)
    void getProductsBasicInfoErrorTest() {
        long id1 = 1;
        long id2 = 2;
        long id4 = 100;
        List<Long> IDsList = Arrays.asList(id1, id2, id4);

        client.post().uri("/products/getBasicInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IDsList)
                .exchange()

                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(message -> assertEquals("Product IDs not found: [100]", message));
    }

    @Test
    @DisplayName("GIVEN a non existent product's ID WHEN the product's controller updateProduct method is called THEN an error should be returned")
    @Order(6)
    void productUpdateThrowExceptionIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.put().uri("/products/" + productService.getLastCreatedID() + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");
    }

    @Test
    @DisplayName("GIVEN a product's information WHEN the product's controller updateProduct method is called with a non existent Id THEN the NonExistingProductException exception is thrown")
    @Order(7)
    void productDeleteThrowExceptionIT() {
        //GIVEN

        client.delete().uri("/products/" + (productService.getLastCreatedID() + 1))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");
    }

    @Test
    @DisplayName("Given a list of products to submit with incorrect IDs, When the endpoint is called Then it should return the exception")
    @Order(8)
    void reduceProductsStockIDsNotFoundIT() {

        client.get().uri("/products/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.stock").isEqualTo(10);

        List<ProductToSubmit> productsToSubmit = List.of(
                new ProductToSubmit(99999L, 5)
        );

        client.post().uri("/products/reduceStock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productsToSubmit)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("Product IDs not found: [99999]");

    }

    @Test
    @DisplayName("Given a list of products to submit with not enough stock, When the endpoint is called, Then it should return the exception")
    @Order(9)
    void submitProductsNotEnoughStockIT() {

        client.get().uri("/products/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.stock").isEqualTo(10);

        List<ProductToSubmit> productsToSubmit = List.of(
                new ProductToSubmit(1L, 15)
        );

        client.post().uri("/products/reduceStock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productsToSubmit)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$").isEqualTo("Product IDs without required stock: [1]");
    }
}
