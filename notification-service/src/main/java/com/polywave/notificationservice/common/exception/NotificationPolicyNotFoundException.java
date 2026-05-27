package com.polywave.notificationservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class NotificationPolicyNotFoundException extends BusinessException {
    public NotificationPolicyNotFoundException() {
        super(NotificationErrorCode.NOTIFICATION_POLICY_NOT_FOUND);
    }
}
