package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.gfttraining.productAPI.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    //
    @DisplayName("WHEN deleteProduct is executed THEN delete a product object")
    void productDeleteIT() {

        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(6);

        client.delete().uri("/products/3")
                .exchange()
                .expectStatus().isOk() // en el udemy el le pone un no content porque es lo que entiendo tiene el configurado
                .expectBody().isEmpty();

        client.get().uri("/products").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(5);

        client.get().uri("/products/3").exchange()
                .expectStatus().isNotFound();
                //.expectBody().isEmpty()

    }

    @Test
    @DisplayName("GIVEN a product's information WHEN the product's controller putUpdate method is called with incorrect Id THEN throws the NonExistingProductException exception")
    void productDeleteThrowExceptionIT() {
        //GIVEN
        //ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.delete().uri("/products/10")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");;
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





    @Test
    @DisplayName("GIVEN2 a product's information WHEN the product's controller putUpdate method is called THEN the provided product's information is updated with te new information")
    void productUpdateThrowExceptionIT() {
        //GIVEN
        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.put().uri("/products/6")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$").isEqualTo("The provided ID is non existent");




    }

}