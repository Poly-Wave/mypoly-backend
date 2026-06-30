package com.polywave.billservice.application.bill.query.result;

import com.polywave.billservice.domain.AgeBand;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record BillVoteDetailResult(
        long agreeCount,
        long disagreeCount,
        long totalVoteCount,
        double agreeRatio,
        double disagreeRatio,
        List<VoteDemographicBreakdownResult> ageBandBreakdown,
        List<VoteDemographicBreakdownResult> genderBreakdown) {

    private static final List<String> GENDER_SEGMENTS = List.of("MAN", "WOMAN");

    public static BillVoteDetailResult of(
            long agreeCount,
            long disagreeCount,
            Map<String, Long> ageBandCounts,
            Map<String, Long> genderCounts
    ) {
        BillVoteSummaryResult voteSummary = BillVoteSummaryResult.of(null, agreeCount, disagreeCount);
        List<String> ageBandSegments = Arrays.stream(AgeBand.values())
                .map(AgeBand::name)
                .toList();

        return new BillVoteDetailResult(
                voteSummary.agreeCount(),
                voteSummary.disagreeCount(),
                voteSummary.totalVoteCount(),
                voteSummary.agreeRatio(),
                voteSummary.disagreeRatio(),
                VoteDemographicBreakdownResult.fromOrderedSegments(ageBandSegments, ageBandCounts),
                VoteDemographicBreakdownResult.fromOrderedSegments(GENDER_SEGMENTS, genderCounts)
        );
    }
}
