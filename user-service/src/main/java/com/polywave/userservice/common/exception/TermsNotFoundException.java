package com.polywave.userservice.common.exception;

public class TermsNotFoundException extends RuntimeException {
    public TermsNotFoundException(Long termsId) {
        super("약관을 찾을 수 없습니다. termsId=" + termsId);
    }
}
