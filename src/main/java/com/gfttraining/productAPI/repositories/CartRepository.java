package com.gfttraining.productAPI.repositories;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
import com.gfttraining.productAPI.model.ProductDTO;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import org.springframework.stereotype.Repository;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
@Setter
public class CartRepository {

    private final RestTemplate restTemplate;
    @Value("${cartMicroservice.url}")
    public String cartServiceUrl;

    @Value("${cartMicroservice.port}")
    public int cartPort;

    public CartRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(retryFor = InvalidCartConnectionException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public ProductDTO updateProduct(ProductDTO productDTO) throws InvalidCartConnectionException, InvalidCartResponseException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<ProductDTO>> requestEntity = new HttpEntity<>(List.of(productDTO), headers);

        try{
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                     "http://"+ cartServiceUrl +":"+ cartPort +"/carts/updateStock/",
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
                );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return productDTO;
            }

            if(responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                throw new InvalidCartConnectionException("Invalid connection with cart microservice");
            }

            throw new InvalidCartResponseException("Invalid cart response: Expected 200 Got: " + responseEntity.getStatusCode());

        } catch (RestClientException ex){
            throw new InvalidCartConnectionException("Invalid connection with cart microservice");
        }

    }

}
