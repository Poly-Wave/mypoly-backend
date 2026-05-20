package com.polywave.notificationservice.application.notification.policy.command;

import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;

/**
 * 알림 정책 생성/수정에 공통으로 사용하는 입력 커맨드.
 * - status 는 별도 상태 변경 API 로 분리한다.
 */
public record NotificationPolicyCommand(
        String policyKey,
        String name,
        String depth,
        NotificationChannel channel,
        NotificationCategory category,
        String targetAudience,
        String sendSchedule,
        String title,
        String body,
        LandingType landingType,
        String landingUrl
) {
}
