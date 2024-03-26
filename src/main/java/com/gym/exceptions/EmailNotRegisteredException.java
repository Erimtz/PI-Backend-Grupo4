package com.gym.exceptions;

public class EmailNotRegisteredException extends RuntimeException {
    public EmailNotRegisteredException(String message) {
        super(message);
    }
}