package com.polywave.userservice.application.user.query.result;

/**
 * 온보딩 리마인더 발급 대상 유저 한 명 (user-service 내부 result 타입).
 *
 * - 행 1, 2 알림의 대상 후보로 산출된다.
 * - notification-service 가 internal API 로 호출해 받아간다.
 * - nickname 은 알림 본문 {별명} 변수 치환에 사용한다.
 */
public record OnboardingReminderUserResult(Long userId, String nickname) {
}
