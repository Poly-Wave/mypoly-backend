package com.polywave.billservice.application.notification.query.result;

/**
 * 행 3 발급 대상: 한 사용자의 관심 카테고리에 매칭되는 신규 안건 개수.
 */
public record UserInterestAgendaCountResult(Long userId, long count) {
}
