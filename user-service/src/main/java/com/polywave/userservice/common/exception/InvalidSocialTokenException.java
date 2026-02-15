package com.polywave.userservice.common.exception;

public class InvalidSocialTokenException extends RuntimeException {
    public InvalidSocialTokenException(String message) {
        super(message);
    }
}
