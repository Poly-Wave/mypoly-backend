package com.polywave.billservice.application.agenda.query.result;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 탭별 안건 목록 조회 결과 한 건.
 * HOT_DEBATE: agreeRatio(찬성 비율), totalVoteCount(해당 기간 찬반 합산 투표 수).
 * TRENDING: totalVoteCount는 배치 스냅샷 기준 최근 N일 투표 수, agreeRatio는 같은 기간 찬성 비율.
 * RECENT_30D: 최근 30일 이내 투표만 집계, 최소 투표 수(쟁쟁한과 동일 설정) 미만 의안 제외.
 * totalVoteCount·agreeRatio는 해당 30일 창 기준.
 * SAME_AGE: totalVoteCount는 최근 N일(쟁쟁한과 동일 bill.agenda.hot-debate.days) 이내
 * 동일 연령대(투표 시점) 투표 완료 수. 노출은 해당 연령대 투표 수가 최소 M건(동일 min-vote-count) 이상인 의안만.
 * agreeRatio는 SAME_AGE 집계와 동일한 기간/연령대 조건의 찬성 비율.
 */
public record AgendaResult(
                Long billId,
                String officialTitle,
                double agreeRatio,
                double disagreeRatio,
                long totalVoteCount,
                boolean hasVoted,
                String categoryCode) {

    public AgendaResult(
            Long billId,
            String officialTitle,
            double agreeRatio,
            long totalVoteCount,
            boolean hasVoted,
            String categoryCode) {
        this(
                billId,
                officialTitle,
                totalVoteCount > 0 ? roundTo2(agreeRatio) : 0.0,
                totalVoteCount > 0 ? 1.0 - roundTo2(agreeRatio) : 0.0,
                totalVoteCount,
                hasVoted,
                categoryCode);
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}