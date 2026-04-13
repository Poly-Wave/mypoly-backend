package com.polywave.billservice.application.agenda.query.result;

/**
 * 탭별 안건 목록 조회 결과 한 건.
 * HOT_DEBATE에서는 agreeRatio, disagreeRatio, totalVoteCount가 채워지고,
 * TRENDING에서는 totalVoteCount에 최근 7일 투표 수가 채워진다.
 */
public record AgendaResult(
        Long billId,
        String officialTitle,
        double agreeRatio,
        double disagreeRatio,
        long totalVoteCount,
        boolean hasVoted,
        String categoryCode
) {
}