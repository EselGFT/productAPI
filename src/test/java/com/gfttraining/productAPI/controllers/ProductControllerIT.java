package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;
import com.gfttraining.productAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductControllerIT {

    @LocalServerPort private int port;
    private WebTestClient client;
    @Autowired private ProductService productService;

    @PostConstruct
    void init() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }

    @Test
    void listAllTest() {

        int numberOfProducts = productService.getNumberOfProducts();

        //Apple
        productService.createProduct(new ProductRequest("Apple", "A rounded food object", "food", 1.25, 23,1.0));

        //Dictionary
        productService.createProduct(new ProductRequest("Dictionary", "A book that defines words", "books", 19.89, 13 ,1.0));

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
                .jsonPath("$[" + ( numberOfProducts + 1) + "].name").isEqualTo("Dictionary")
                .jsonPath("$[" + ( numberOfProducts + 1) + "].description").isEqualTo("A book that defines words")
                .jsonPath("$[" + ( numberOfProducts + 1) + "].category.name").isEqualTo("books")
                .jsonPath("$[" + ( numberOfProducts + 1) + "].price").isEqualTo(19.89)
                .jsonPath("$[" + ( numberOfProducts + 1) + "].stock").isEqualTo(13)
                .jsonPath("$[" + ( numberOfProducts + 1) + "].weight").isEqualTo(1.0);
    }

    @Test
    void listOneProductByIDTest() {
        //Apple
        productService.createProduct(new ProductRequest("Apple", "A rounded food object", "food", 1.25, 23,1.0));

        int appleID = productService.getNumberOfProducts();

        client.get().uri("/products/" + appleID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Apple")
                .jsonPath("$.description").isEqualTo("A rounded food object")
                .jsonPath("$.category.name").isEqualTo("food")
                .jsonPath("$.price").isEqualTo(1.25)
                .jsonPath("$.stock").isEqualTo(23)
                .jsonPath("$.weight").isEqualTo(1.0);
    }

    @Test
    void listOneNonExistentProductTest() {
        int numberOfProducts = productService.getNumberOfProducts();

        client.get().uri("/products/" + (numberOfProducts + 1))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");
    }

    @Test
    void listOneProductWithStringIDTest() {
        client.get().uri("/products/str")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$").isEqualTo("Wrong type exception, please consult the OpenAPI documentation");
    }
}
