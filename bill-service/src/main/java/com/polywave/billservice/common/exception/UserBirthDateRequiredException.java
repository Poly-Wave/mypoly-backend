package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class UserBirthDateRequiredException extends BusinessException {
    public UserBirthDateRequiredException() {
        super(BillErrorCode.USER_BIRTH_DATE_REQUIRED);
    }
}
