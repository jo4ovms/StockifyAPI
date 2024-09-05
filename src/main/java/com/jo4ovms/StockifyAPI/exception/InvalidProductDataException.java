package com.jo4ovms.StockifyAPI.exception;

public class InvalidProductDataException extends RuntimeException {

    public InvalidProductDataException(String message) {
        super(message);
    }
}