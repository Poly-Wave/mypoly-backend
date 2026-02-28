package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class UserValidationException extends BusinessException {
    public UserValidationException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
