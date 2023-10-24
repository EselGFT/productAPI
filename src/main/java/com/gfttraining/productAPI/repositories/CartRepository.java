package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.model.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class CartRepository {

    private final RestTemplate restTemplate;

    @Value("${cartMicroservice.url}")
    private String cartServiceUrl;

    @Value("${cartMicroservice.port}")
    private int port;

    private final String baseUri;

    public CartRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        baseUri = String.format("http://%s:%d", cartServiceUrl, port);
    }

    public ProductDTO updateProduct(ProductDTO productDTO) throws InvalidCartConnectionException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> requestEntity = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                baseUri + "/carts/updateStock/",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return productDTO;
        }

        throw new InvalidCartConnectionException("Could not connect with cart microservice");
    }

}