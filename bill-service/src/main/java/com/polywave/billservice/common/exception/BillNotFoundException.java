package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class BillNotFoundException extends BusinessException {
    public BillNotFoundException() {
        super(BillErrorCode.BILL_NOT_FOUND);
    }
}
