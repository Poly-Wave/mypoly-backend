package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "의안 투표 요약 응답")
public record BillVoteSummaryResponse(
        @Schema(description = "현재 사용자의 투표 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
        boolean hasVoted,

        @Schema(
                description = "현재 사용자의 투표값. hasVoted=false인 경우 아직 투표하지 않은 상태이므로 null입니다. 가능한 값: AGREE, DISAGREE",
                example = "AGREE",
                nullable = true,
                requiredMode = RequiredMode.REQUIRED
        )
        String myVoteResult,

        @Schema(description = "찬성 수", example = "120", requiredMode = RequiredMode.REQUIRED)
        long agreeCount,

        @Schema(description = "반대 수", example = "80", requiredMode = RequiredMode.REQUIRED)
        long disagreeCount,

        @Schema(description = "총 투표 수", example = "200", requiredMode = RequiredMode.REQUIRED)
        long totalVoteCount,

        @Schema(description = "찬성 비율 (0~1)", example = "0.60", requiredMode = RequiredMode.REQUIRED)
        double agreeRatio,

        @Schema(description = "반대 비율 (0~1)", example = "0.40", requiredMode = RequiredMode.REQUIRED)
        double disagreeRatio
) {
    public static BillVoteSummaryResponse from(BillVoteSummaryResult result) {
        return new BillVoteSummaryResponse(
                result.hasVoted(),
                result.myVoteResult(),
                result.agreeCount(),
                result.disagreeCount(),
                result.totalVoteCount(),
                result.agreeRatio(),
                result.disagreeRatio()
        );
    }
}