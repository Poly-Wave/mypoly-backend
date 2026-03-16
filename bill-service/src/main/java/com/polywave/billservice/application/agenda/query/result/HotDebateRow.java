package com.polywave.billservice.application.agenda.query.result;

import java.time.LocalDate;

/**
 * QueryDSL 쟁쟁한 안건 집계 결과 한 행.
 * (bill id, 제목 등 + 찬성/반대 카운트)
 */
public record HotDebateRow(
        Long billId,
        String officialTitle,
        LocalDate proposalDate,
        String detailUrl,
        Long agreeCount,
        Long disagreeCount
) {
    public long totalVoteCount() {
        return agreeCount + disagreeCount;
    }

    public double agreeRatio() {
        long total = totalVoteCount();
        return total == 0 ? 0 : (double) agreeCount / total;
    }

    public double disagreeRatio() {
        long total = totalVoteCount();
        return total == 0 ? 0 : (double) disagreeCount / total;
    }

    public AgendaResult toAgendaResult() {
        return new AgendaResult(
                billId,
                officialTitle,
                proposalDate,
                detailUrl,
                agreeRatio(),
                disagreeRatio(),
                totalVoteCount()
        );
    }
}
