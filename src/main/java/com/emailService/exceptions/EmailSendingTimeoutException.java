package com.emailService.exceptions;

public class EmailSendingTimeoutException extends RuntimeException {
    public EmailSendingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
