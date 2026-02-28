package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class UnsupportedSocialLoginException extends BusinessException {
    public UnsupportedSocialLoginException() {
        super(UserErrorCode.UNSUPPORTED_SOCIAL_LOGIN);
    }
}
