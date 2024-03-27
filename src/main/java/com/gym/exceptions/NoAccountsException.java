package com.gym.exceptions;

public class NoAccountsException extends RuntimeException {

    public NoAccountsException(String message) {
        super(message);
    }

    public NoAccountsException(String message, Throwable cause) {
        super(message, cause);
    }
}