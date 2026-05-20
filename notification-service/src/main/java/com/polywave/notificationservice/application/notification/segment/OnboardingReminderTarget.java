package com.polywave.notificationservice.application.notification.segment;

/**
 * 온보딩 리마인더 발급 대상 한 명.
 * - userId: 발급 대상
 * - nickname: 본문 템플릿 변수 치환용 (없으면 빈 문자열)
 */
public record OnboardingReminderTarget(Long userId, String nickname) {
}
