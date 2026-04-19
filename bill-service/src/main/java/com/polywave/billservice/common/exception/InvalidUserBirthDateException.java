package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidUserBirthDateException extends BusinessException {
    public InvalidUserBirthDateException() {
        super(BillErrorCode.INVALID_USER_BIRTH_DATE);
    }
}
