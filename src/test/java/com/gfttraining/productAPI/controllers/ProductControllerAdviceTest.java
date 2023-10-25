package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

public class ProductControllerAdviceTest {

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock
    private BindingResult bindingResult;

    @Mock
    MethodArgumentNotValidException methodArgumentNotValidException ;

    private ProductControllerAdvice productControllerAdvice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productControllerAdvice = new ProductControllerAdvice();
    }

    @Test
    void testHandleConstraintViolationException() {
        Set<ConstraintViolation<?>> violations = Set.of(constraintViolation);
        ConstraintViolationException constraintViolationException = new ConstraintViolationException(violations);

        Mockito.when(constraintViolation.getMessage()).thenReturn("Stock should not be null");
        ResponseEntity<?> response = productControllerAdvice.handleConstraintViolationException(constraintViolationException);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        ResponseEntity<String> response = productControllerAdvice.handleHttpMessageNotReadableException();
        assertEquals("Request parameters have a wrong format, please consult the OpenAPI documentation", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleNumberFormatException() {
        ResponseEntity<String> response = productControllerAdvice.handleNumberFormatException();
        assertEquals("Your input does not match the one required by the endpoint, please refer to the OpenAPI documentation", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> response = productControllerAdvice.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() {
        ResponseEntity<String> response = productControllerAdvice.handleMethodArgumentTypeMismatchException();
        assertEquals("Wrong type exception, please consult the OpenAPI documentation", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleNonExistingProductException() {
        NonExistingProductException ex = new NonExistingProductException("Product IDs not found: [1]");
        ResponseEntity<String> response = productControllerAdvice.handleNonExistingProductException(ex);
        assertEquals("Product IDs not found: [1]", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testNotEnoughStockException() {
        NotEnoughStockException ex = new NotEnoughStockException("Product IDs without required stock: [1]");
        ResponseEntity<String> response = productControllerAdvice.handleNotEnoughStockException(ex);
        assertEquals("Product IDs without required stock: [1]", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testInvalidCartConnectionException() {
        InvalidCartConnectionException ex = new InvalidCartConnectionException("Invalid connection with cart microservice");
        ResponseEntity<String> response = productControllerAdvice.handleInvalidCartConnectionException(ex);
        assertEquals("Invalid connection with cart microservice", response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testInvalidCartResponseException() {
        InvalidCartResponseException ex = new InvalidCartResponseException("Invalid cart response: Expected 200 Got: 400");
        ResponseEntity<String> response = productControllerAdvice.handleInvalidCartResponseException(ex);
        assertEquals("Invalid cart response: Expected 200 Got: 400", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
