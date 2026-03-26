package com.polywave.billservice.application.agenda.query.result;


/**
 * 탭별 안건 목록 조회 결과 한 건.
 * 쟁쟁한 탭에서는 agreeRatio, disagreeRatio, totalVoteCount가 채워짐.
 */
public record AgendaResult(
        Long billId,
        String officialTitle,
        double agreeRatio,
        double disagreeRatio,
        long totalVoteCount,
        boolean hasVoted
) {
}
