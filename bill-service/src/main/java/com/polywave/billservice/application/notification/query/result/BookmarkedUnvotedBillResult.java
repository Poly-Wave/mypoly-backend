package com.polywave.billservice.application.notification.query.result;

/**
 * 알림 발급 대상으로 산출된 (사용자, 의안) 쌍.
 *
 * - 행 7 (북마크 D+1 미투표) 의 발급 대상이다.
 * - notification-service 가 internal API 로 받아간다.
 */
public record BookmarkedUnvotedBillResult(
        Long userId,
        Long billId,
        String billTitle
) {
}
