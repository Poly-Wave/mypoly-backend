package com.polywave.billservice.application.notification.query.result;

/**
 * 행 6 발급 대상: 북마크된 의안의 단계 전이.
 *
 * - fromStageCode/Name 이 null 이면 의안의 최초 단계 (이전 단계 없음).
 */
public record BillStageChangeResult(
        Long userId,
        Long billId,
        String billTitle,
        String fromStageCode,
        String fromStageName,
        String toStageCode,
        String toStageName
) {
}
