package com.polywave.billservice.application.bill.query.result;

public record BillVoteSummaryResult(
        boolean hasVoted,
        String myVoteResult,
        long agreeCount,
        long disagreeCount,
        long totalVoteCount,
        double agreeRatio
) {
    public static BillVoteSummaryResult of(String myVoteResult, long agreeCount, long disagreeCount) {
        long totalVoteCount = agreeCount + disagreeCount;
        double agreeRatio = totalVoteCount == 0 ? 0.0 : (double) agreeCount / totalVoteCount;

        return new BillVoteSummaryResult(
                myVoteResult != null,
                myVoteResult,
                agreeCount,
                disagreeCount,
                totalVoteCount,
                agreeRatio
        );
    }
}