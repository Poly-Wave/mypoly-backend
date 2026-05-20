package com.polywave.notificationservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class InvalidNotificationPolicyException extends BusinessException {
    public InvalidNotificationPolicyException() {
        super(NotificationErrorCode.INVALID_NOTIFICATION_POLICY);
    }
}
