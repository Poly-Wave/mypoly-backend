package com.polywave.userservice.common.exception;

public class UnsupportedSocialLoginException extends RuntimeException {
    public UnsupportedSocialLoginException(String message) {
        super(message);
    }
}
