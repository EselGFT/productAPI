package com.gfttraining.productAPI.exceptions;

public class NotEnoughStockException extends Throwable {
    public NotEnoughStockException(String message) {
        super(message);
    }
}
