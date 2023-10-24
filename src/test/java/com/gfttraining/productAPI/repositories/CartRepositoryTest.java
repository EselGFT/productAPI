package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.model.ProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartRepositoryTest {

    @Mock
    RestTemplate restTemplate;

    @Value("${cartMicroservice.url}")
    private String cartServiceUrl;

    @Value("${cartMicroservice.port}")
    private int port;

    private final String baseUri;

    private final CartRepository cartRepository;

    public CartRepositoryTest() {
        MockitoAnnotations.openMocks(this);
        cartRepository = new CartRepository(restTemplate);
        baseUri = String.format("http://%s:%d", cartServiceUrl, port);
    }

    @Test
    @DisplayName("GIVEN a proper connection with the microservice WHEN sending the update request THEN the correct DTO is returned")
    void updateProductTest() throws InvalidCartConnectionException {
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
                baseUri + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenReturn(response);

        ProductDTO productDTOResponse = cartRepository.updateProduct(productDTO);

        assertEquals(productDTOResponse, productDTO);

    }

    @Test
    @DisplayName("GIVEN a bad connection with the microservice WHEN sending the update request THEN the InvalidCartConnectionException is invoked")
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
                baseUri + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        )).thenReturn(response);

       assertThrows(InvalidCartConnectionException.class, () -> cartRepository.updateProduct(productDTO));



    }
}
