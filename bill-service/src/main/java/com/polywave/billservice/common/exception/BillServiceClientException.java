package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class BillServiceClientException extends BusinessException {
    public BillServiceClientException(BillErrorCode errorCode) {
        super(errorCode);
    }
}
