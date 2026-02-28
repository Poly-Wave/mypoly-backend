package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidOnboardingStatusException extends BusinessException {
    public InvalidOnboardingStatusException() {
        super(UserErrorCode.INVALID_ONBOARDING_STATUS_FOR_PROFILE_UPDATE);
    }
}
