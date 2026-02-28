package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class DuplicateNicknameException extends BusinessException {
    public DuplicateNicknameException() {
        super(UserErrorCode.DUPLICATE_NICKNAME);
    }
}
