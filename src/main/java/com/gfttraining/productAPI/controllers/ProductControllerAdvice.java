package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ProductControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
    
        StringBuilder errorMessage = new StringBuilder("Validation error(s):\n");
        ex.getConstraintViolations().forEach(violation ->
                errorMessage.append(" ").append(violation.getMessage()).append(";\n")
        );

        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        StringBuilder errorMessage = new StringBuilder("Validation error(s):\n");
        ex.getBindingResult().getAllErrors().forEach(error -> errorMessage.append(" ").append(error.getDefaultMessage()).append(";\n"));

        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleNumberFormatException() {
        return new ResponseEntity<>("Your input does not match the one required by the endpoint, " +
                "please refer to the OpenAPI documentation", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleHttpMessageNotReadableException() {
         return new ResponseEntity<>("Request parameters have a wrong format, please consult the OpenAPI documentation", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException() {
         return new ResponseEntity<>("Wrong type exception, please consult the OpenAPI documentation", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotEnoughStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleNotEnoughStockException(NotEnoughStockException ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonExistingProductException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNonExistingProductException(NonExistingProductException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCartConnectionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleInvalidCartConnectionException(InvalidCartConnectionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
