package com.polywave.notificationservice.application.notification.push;

import com.polywave.notificationservice.domain.notification.UserNotification;

/**
 * 실제 푸시 발송을 추상화한 포트.
 *
 * - 이번 범위에서는 FCM/APNS 연동을 하지 않고 No-op 구현체만 제공한다.
 * - 추후 FCM 구현체로 교체하기 쉽도록 인터페이스 경계만 잡아둔다.
 * - 별도 토큰 관리, credential 처리 등은 본 인터페이스 밖에서 다룬다.
 */
public interface PushSender {

    void send(UserNotification notification);
}
