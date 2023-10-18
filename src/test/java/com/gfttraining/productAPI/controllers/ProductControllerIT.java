package com.gfttraining.productAPI.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerIT {

    private ObjectMapper objectMapper;
    @LocalServerPort private int port;

    private WebTestClient client;

    @BeforeEach
    void setUp () {
        objectMapper = new ObjectMapper();


    }

    @PostConstruct
    void init() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }
    @Test
    @DisplayName("GIVEN a product's information WHEN the product's controller putUpdate method is called THEN the provided product's information is updated with te new information")
    void productUpdateIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.get().uri("/products/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product product = response.getResponseBody();
                    assertEquals("books",product.getCategory().getName());
                    assertEquals("Small book",product.getDescription());
                    assertEquals("Book",product.getName());
                    assertEquals(5.0,product.getPrice());
                    assertEquals(20,product.getStock());
                    assertEquals(1.0,product.getWeight());
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
                    assertEquals("TestProduct",afterUpdateProduct.getName());
                    assertEquals("",afterUpdateProduct.getDescription());
                    assertEquals("other",afterUpdateProduct.getCategory().getName());
                    assertEquals(10.0,afterUpdateProduct.getPrice());
                    assertEquals(50,afterUpdateProduct.getStock());
                    assertEquals(2.0,afterUpdateProduct.getWeight());

                });
    }





    @Test
    @DisplayName("WHEN deleteProduct is executed THEN delete a product object")
    void productDeleteIT() {

        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(3);

        client.delete().uri("/products/3")
                .exchange()
                .expectStatus().isOk() // en el udemy el le pone un no content porque es lo que entiendo tiene el configurado
                .expectBody().isEmpty();

        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(2);

        client.get().uri("/products/3").exchange()
                .expectStatus().isNotFound();
                //.expectBody().isEmpty()

    }

    @Test
    @DisplayName("GIVEN a product's information WHEN the product's controller putUpdate method is called with incorrect Id THEN throws the NonExistingProductException exception")
    void productDeleteThrowExceptionIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.delete().uri("/products/6")
                .exchange()
                .expectStatus().isNotFound() // en el udemy el le pone un no content porque es lo que entiendo tiene el configurado
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");
    }




    @Test
    @DisplayName("GIVEN2 a product's information WHEN the product's controller putUpdate method is called THEN the provided product's information is updated with te new information")
    void productUpdateThrowExceptionIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.put().uri("/products/6")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");



    }

}