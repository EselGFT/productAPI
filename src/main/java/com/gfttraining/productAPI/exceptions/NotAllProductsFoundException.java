package com.gfttraining.productAPI.exceptions;

public class NotAllProductsFoundException extends Exception {
    public NotAllProductsFoundException(){
        super();
    }

    public NotAllProductsFoundException(String message){
        super(message);
    }
}
