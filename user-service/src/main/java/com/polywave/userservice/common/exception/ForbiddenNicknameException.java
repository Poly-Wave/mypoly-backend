package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class ForbiddenNicknameException extends BusinessException {
    public ForbiddenNicknameException() {
        super(UserErrorCode.FORBIDDEN_NICKNAME);
    }
}
