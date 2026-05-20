package com.polywave.notificationservice.domain.notification;

/**
 * 알림 정책(노션 "알림/푸시 리스트") 운영 상태.
 *
 * - READY    : 시작 전 (노션 기본값과 매칭)
 * - ACTIVE   : 활성 — 발송 대상으로 추출/발송 가능
 * - INACTIVE : 비활성 — 발송 중단
 */
public enum NotificationPolicyStatus {
    READY,
    ACTIVE,
    INACTIVE
}
