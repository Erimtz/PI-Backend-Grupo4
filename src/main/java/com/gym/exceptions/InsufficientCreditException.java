package com.gym.exceptions;

public class InsufficientCreditException extends RuntimeException {

    public InsufficientCreditException() {
        super();
    }

    public InsufficientCreditException(String message) {
        super(message);
    }

    public InsufficientCreditException(String message, Throwable cause) {
        super(message, cause);
    }
}