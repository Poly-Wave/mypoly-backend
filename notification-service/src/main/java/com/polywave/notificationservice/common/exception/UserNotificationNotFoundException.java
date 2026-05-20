package com.polywave.notificationservice.common.exception;

import com.polywave.common.exception.BusinessException;

public class UserNotificationNotFoundException extends BusinessException {
    public UserNotificationNotFoundException() {
        super(NotificationErrorCode.USER_NOTIFICATION_NOT_FOUND);
    }
}
