package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
import com.gfttraining.productAPI.model.ProductDTO;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

public class CartRepositoryTest {

    @Mock
    RestTemplate restTemplate;

    public String cartServiceUrl = "localhost";

    public int cartPort = 8085;

    private final CartRepository cartRepository;

    public CartRepositoryTest() {
        MockitoAnnotations.openMocks(this);
        cartRepository = new CartRepository(restTemplate);
        cartRepository.setCartPort(cartPort);
        cartRepository.setCartServiceUrl(cartServiceUrl);
    }

    @Test
    @DisplayName("GIVEN a proper connection with the microservice WHEN sending the update request THEN the correct DTO is returned")
    void updateProductTest() throws InvalidCartConnectionException, InvalidCartResponseException {

        BigDecimal newPrice = new BigDecimal("10.00");
        BigDecimal newPriceRounded = newPrice.setScale(2, RoundingMode.CEILING);

        ProductDTO productDTO = new ProductDTO(
                1L,
                newPriceRounded,
                50,
                1.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);

        Mockito.when(restTemplate.exchange(
                "http://" + cartServiceUrl + ":" + cartPort + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenReturn(response);

        ProductDTO productDTOResponse = cartRepository.updateProduct(productDTO);

        assertEquals(productDTOResponse, productDTO);
    }

    @Test
    @DisplayName("GIVEN a bad response of the cart microservice WHEN sending the update request THEN the InvalidCartResponseException is invoked")
    void updateProductExceptionTest() {
        BigDecimal newPrice = new BigDecimal("10.00");
        BigDecimal newPriceRounded = newPrice.setScale(2, RoundingMode.CEILING);

        ProductDTO productDTO = new ProductDTO(
                1L,
                newPriceRounded,
                50,
                1.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Mockito.when(restTemplate.exchange(
                "http://" + cartServiceUrl + ":" + cartPort + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenReturn(response);

        assertThrows(InvalidCartResponseException.class,() ->cartRepository.updateProduct(productDTO));

    }
    @Test
    @DisplayName("GIVEN a bad connection with the microservice WHEN sending the update request THEN the InvalidCartConnectionException is invoked")
    void updateProduct500ExceptionTest() {
        BigDecimal newPrice = new BigDecimal("10.00");
        BigDecimal newPriceRounded = newPrice.setScale(2, RoundingMode.CEILING);

        ProductDTO productDTO = new ProductDTO(
                1L,
                newPriceRounded,
                50,
                1.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(restTemplate.exchange(
                "http://" + cartServiceUrl + ":" + cartPort + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenReturn(response);

        assertThrows(InvalidCartConnectionException.class,() ->cartRepository.updateProduct(productDTO));

    }

    @Test
    @DisplayName("GIVEN an attempt to connect to cart microservice WHEN sending the update request and restClientException is thrown THEN the InvalidCartConnectionException is invoked")
    void updateProductRestClientExceptionTest() {
        BigDecimal newPrice = new BigDecimal("10.00");
        BigDecimal newPriceRounded = newPrice.setScale(2, RoundingMode.CEILING);

        ProductDTO productDTO = new ProductDTO(
                1L,
                newPriceRounded,
                50,
                1.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);



        Mockito.when(restTemplate.exchange(
                "http://" + cartServiceUrl + ":" + cartPort + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenThrow(RestClientException.class);

        assertThrows(InvalidCartConnectionException.class,() ->cartRepository.updateProduct(productDTO));

    }
}
