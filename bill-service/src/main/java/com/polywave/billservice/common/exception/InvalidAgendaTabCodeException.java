package com.polywave.billservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidAgendaTabCodeException extends BusinessException {
    public InvalidAgendaTabCodeException() {
        super(BillErrorCode.INVALID_AGENDA_TAB_CODE);
    }
}
