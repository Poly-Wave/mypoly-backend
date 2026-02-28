package com.polywave.common.exception;

import lombok.Getter;

@Getter
public class JwtSecretConfigurationException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtSecretConfigurationException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }
}
