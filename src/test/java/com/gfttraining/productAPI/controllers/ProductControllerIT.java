package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.ProductRequest;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIT {

    @Autowired
    private WebTestClient client;


    @Test
    @DisplayName("Given a list of IDs When a post is made to /productsWithIDs Then it should return the ProductDTO of the found IDs")
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
    @DisplayName("Given a list of IDs When a post is made to /productsWithIDs Then it should return the ProductDTO of the found IDs")
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
    @DisplayName("Given a list of productRquests When a post is made to /products Then it should be saved in database and return the saved products")
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

    @Test
    @DisplayName("Given a list of productRequests with bad content When a post is made to /products Then it should return an error message")
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
    @DisplayName("Given a productRequests  When a post is made to /product Then it should be saved in the database and return the saved product")
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

    @Test
    @DisplayName("Given a productRequest with bad content When a post is made to /product Then it should return an error message")
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
}
