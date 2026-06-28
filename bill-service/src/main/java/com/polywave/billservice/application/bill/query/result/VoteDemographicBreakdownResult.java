package com.polywave.billservice.application.bill.query.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record VoteDemographicBreakdownResult(
        String segment,
        long count,
        double ratio) {

    public static List<VoteDemographicBreakdownResult> fromOrderedSegments(
            List<String> orderedSegments,
            Map<String, Long> countsBySegment
    ) {
        long segmentTotal = orderedSegments.stream()
                .mapToLong(segment -> countsBySegment.getOrDefault(segment, 0L))
                .sum();

        List<VoteDemographicBreakdownResult> results = new ArrayList<>();
        double accumulatedRatio = 0.0;

        for (int i = 0; i < orderedSegments.size(); i++) {
            String segment = orderedSegments.get(i);
            long count = countsBySegment.getOrDefault(segment, 0L);
            double ratio = resolveRatio(count, segmentTotal, i == orderedSegments.size() - 1, accumulatedRatio);
            accumulatedRatio += ratio;
            results.add(new VoteDemographicBreakdownResult(segment, count, ratio));
        }

        return results;
    }

    private static double resolveRatio(long count, long segmentTotal, boolean isLast, double accumulatedRatio) {
        if (segmentTotal == 0 || count == 0) {
            return 0.0;
        }
        if (isLast) {
            return roundTo2(1.0 - accumulatedRatio);
        }
        return roundTo2((double) count / segmentTotal);
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
