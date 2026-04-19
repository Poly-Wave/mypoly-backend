package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class BillMemberNotFoundException extends BusinessException {

    public BillMemberNotFoundException() {
        super(BillErrorCode.BILL_MEMBER_NOT_FOUND);
    }
}