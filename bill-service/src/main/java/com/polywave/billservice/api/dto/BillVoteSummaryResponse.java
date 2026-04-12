package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Schema(description = "의안 투표 요약 응답")
public record BillVoteSummaryResponse(
        @Schema(description = "현재 사용자의 투표 여부", example = "true")
        boolean hasVoted,

        @Schema(description = "현재 사용자의 투표값", example = "AGREE", nullable = true)
        String myVoteResult,

        @Schema(description = "찬성 수", example = "120")
        long agreeCount,

        @Schema(description = "반대 수", example = "80")
        long disagreeCount,

        @Schema(description = "총 투표 수", example = "200")
        long totalVoteCount,

        @Schema(description = "찬성 비율 (0~1)", example = "0.60")
        double agreeRatio,

        @Schema(description = "반대 비율 (0~1)", example = "0.40")
        double disagreeRatio
) {
    public static BillVoteSummaryResponse from(BillVoteSummaryResult result) {
        double agreeRatio = result.agreeRatio();
        double disagreeRatio = result.disagreeRatio();

        if (result.totalVoteCount() > 0) {
            agreeRatio = roundTo2(agreeRatio);
            disagreeRatio = roundTo2(1.0 - agreeRatio);
        }

        return new BillVoteSummaryResponse(
                result.hasVoted(),
                result.myVoteResult(),
                result.agreeCount(),
                result.disagreeCount(),
                result.totalVoteCount(),
                agreeRatio,
                disagreeRatio
        );
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}