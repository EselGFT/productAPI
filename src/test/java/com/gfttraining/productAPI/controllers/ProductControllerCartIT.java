package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerCartIT {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    private static WireMockServer wireMockServer;

    protected static int cartPort;

    @Value("${cartMicroservice.port}")
    public void setCartPort(int cartPort) {                 // Sets the cart microservice port, located in the application.yaml file
        ProductControllerCartIT.cartPort = cartPort;
    }

    @PostConstruct
    void init() {                                           // Initiates the web client
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }

    @BeforeAll
    static void setUp() {                                   // Starts the wireMockServer
        wireMockServer = new WireMockServer(cartPort);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @AfterEach
    void resetAll() {
        wireMockServer.resetAll();
    }

    @Test
    @Order(1)
    @DisplayName("GIVEN a product's information WHEN the product's controller updateProduct method is called THEN the provided product's information is updated with te new information")
    void productUpdateIT() {

        wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/carts/updateStock/"))
                .willReturn(aResponse().withStatus(200)));

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
    @Order(2)
    @DisplayName("GIVEN a product's information WHEN the product's controller putUpdate method is called, it tries to connect to cart microservice but it fails THEN it throws exception")
    void productUpdateCartExceptionIT() {

        wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/carts/updateStock/"))
                .willReturn(aResponse().withStatus(500)));

        ProductRequest productRequestTest = new ProductRequest("TestProduct", "", "TestCategory", 10.0, 50, 2.0);

        client.get().uri("/products/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product product = response.getResponseBody();
                    assertEquals("TestProduct", product.getName());
                    assertEquals("",product.getDescription());
                    assertEquals("other", product.getCategory().getName());
                    assertEquals(10.0, product.getPrice());
                    assertEquals(50, product.getStock());
                    assertEquals(2.0, product.getWeight());
                });

        client.put().uri("/products/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequestTest)
                .exchange() // THEN
                .expectStatus().is5xxServerError();
    }
}
