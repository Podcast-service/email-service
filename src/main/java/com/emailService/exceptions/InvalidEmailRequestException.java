package com.emailService.exceptions;

public class InvalidEmailRequestException extends RuntimeException {
    public InvalidEmailRequestException(String message) {
        super(message);
    }
}
