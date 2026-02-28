package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidOnboardingStatusException extends BusinessException {
    public InvalidOnboardingStatusException() {
        super(BillErrorCode.INVALID_ONBOARDING_STATUS);
    }
}
