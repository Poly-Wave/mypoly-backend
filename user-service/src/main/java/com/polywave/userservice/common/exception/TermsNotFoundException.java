package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class TermsNotFoundException extends BusinessException {
    public TermsNotFoundException() {
        super(UserErrorCode.TERMS_NOT_FOUND);
    }
}
