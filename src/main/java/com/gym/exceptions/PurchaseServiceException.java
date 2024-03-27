package com.gym.exceptions;

public class PurchaseServiceException extends RuntimeException {

    public PurchaseServiceException(String message) {
        super(message);
    }

    public PurchaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}