package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidSocialTokenException extends BusinessException {
    public InvalidSocialTokenException() {
        super(UserErrorCode.INVALID_SOCIAL_TOKEN);
    }
}
