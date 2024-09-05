package com.jo4ovms.StockifyAPI.exception;

public class DuplicateResourceException extends RuntimeException  {
    public DuplicateResourceException(String message) {
        super(message);
    }
}