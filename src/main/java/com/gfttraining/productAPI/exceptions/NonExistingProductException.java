package com.gfttraining.productAPI.exceptions;

public class NonExistingProductException extends Exception{
    public NonExistingProductException(String msg) {
        super(msg);
    }
}
