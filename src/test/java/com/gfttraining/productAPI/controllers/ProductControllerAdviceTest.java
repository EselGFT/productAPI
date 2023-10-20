package com.gfttraining.productAPI.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.gfttraining.productAPI.exceptions.NotAllProductsFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

public class ProductControllerAdviceTest {

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock 
    private MethodParameter methodParameter;

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
        
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(null, null, null);
        ResponseEntity<String> response = productControllerAdvice.handleHttpMessageNotReadableException(ex);
        assertEquals("Request parameters have a wrong format, please consult the OpenAPI documentation", response.getBody());
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
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(null, null, null, null, null);
        ResponseEntity<String> response = productControllerAdvice.handleMethodArgumentTypeMismatchException(ex);
        assertEquals("Wrong type exception, please consult the OpenAPI documentation", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleNotAllProductsFoundException() {
        NotAllProductsFoundException ex = new NotAllProductsFoundException("Product IDs not found: [1]");
        ResponseEntity<String> response = productControllerAdvice.handleNotAllProductsFoundException(ex);
        assertEquals("Product IDs not found: [1]", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testNotEnoughStockException() {
        NotEnoughStockException ex = new NotEnoughStockException("Product IDs without required stock: [1]");
        ResponseEntity<String> response = productControllerAdvice.handleNotEnoughStockException(ex);
        assertEquals("Product IDs without required stock: [1]", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
