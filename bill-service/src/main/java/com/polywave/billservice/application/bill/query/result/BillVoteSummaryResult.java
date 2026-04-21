package com.polywave.billservice.application.bill.query.result;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record BillVoteSummaryResult(
        boolean hasVoted,
        String myVoteResult,
        long agreeCount,
        long disagreeCount,
        long totalVoteCount,
        double agreeRatio,
        double disagreeRatio) {
    public static BillVoteSummaryResult of(String myVoteResult, long agreeCount, long disagreeCount) {
        long totalVoteCount = agreeCount + disagreeCount;
        double rawAgreeRatio = totalVoteCount == 0 ? 0.0 : (double) agreeCount / totalVoteCount;
        
        double agreeRatio = totalVoteCount == 0 ? 0.0 : roundTo2(rawAgreeRatio);
        double disagreeRatio = totalVoteCount == 0 ? 0.0 : 1.0 - agreeRatio;

        return new BillVoteSummaryResult(
                myVoteResult != null,
                myVoteResult,
                agreeCount,
                disagreeCount,
                totalVoteCount,
                agreeRatio,
                disagreeRatio);
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}