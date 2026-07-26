package com.polywave.notificationservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class NoticeNotFoundException extends BusinessException {
    public NoticeNotFoundException() {
        super(NotificationErrorCode.NOTICE_NOT_FOUND);
    }
}
