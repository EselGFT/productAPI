package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.model.ProductDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@Setter
public class CartRepository {

    RestTemplate restTemplate;

    @Value("${cartMicroservice.url}")
    public String cartServiceUrl;

    @Value("${cartMicroservice.port}")
    public int cartPort;

    public CartRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductDTO updateProduct(ProductDTO productDTO) throws InvalidCartConnectionException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://" + cartServiceUrl + ":" + cartPort + "/carts/updateStock/",
                HttpMethod.PUT,
                request,
                Void.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return productDTO;
        }

        throw new InvalidCartConnectionException("Could not connect with cart microservice");
    }

}