package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.model.ProductDTO;
import com.gfttraining.productAPI.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartRepositoryTest {

    @Mock
    RestTemplate restTemplate;

    private CartRepository cartRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartRepository = new CartRepository(restTemplate);
    }

    @Test
    void updateProductTest() throws InvalidCartConnectionException {
        BigDecimal bd = new BigDecimal("10.00");
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);
        ProductDTO productDTO = new ProductDTO(
                1L,
                roundedPrice,
                50,
                1.0
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> requestEntity = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);

        Mockito.when(restTemplate.exchange(
                cartRepository.externalServiceUrl + "/carts/updateStock/",
                HttpMethod.POST,
                requestEntity,
                Void.class
        )).thenReturn(response);

        ProductDTO productDTOResponse = cartRepository.updateProduct(productDTO);

        assertEquals(productDTOResponse, productDTO);

    }
    @Test
    void updateProductExceptionTest() throws InvalidCartConnectionException {
        BigDecimal bd = new BigDecimal("10.00");
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);
        ProductDTO productDTO = new ProductDTO(
                1L,
                roundedPrice,
                50,
                1.0
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> requestEntity = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Mockito.when(restTemplate.exchange(
                cartRepository.externalServiceUrl + "/carts/updateStock/",
                HttpMethod.POST,
                requestEntity,
                Void.class
        )).thenReturn(response);

       assertThrows(InvalidCartConnectionException.class,() ->cartRepository.updateProduct(productDTO));



    }
}
