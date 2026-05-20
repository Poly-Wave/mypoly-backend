package com.polywave.notificationservice.domain.notification;

/**
 * 알림 전달 채널.
 * - IN_APP : 앱 내 알림함에만 저장되는 알림
 * - PUSH   : 푸시 발송 + 앱 내 알림함에도 동일하게 저장되는 알림
 */
public enum NotificationChannel {
    IN_APP,
    PUSH
}
