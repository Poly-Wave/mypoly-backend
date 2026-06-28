package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillVoteDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;

@Schema(description = "의안 투표 상세(인구통계) 응답")
public record BillVoteDetailResponse(
        @Schema(description = "찬성 수", example = "120", requiredMode = RequiredMode.REQUIRED)
        long agreeCount,

        @Schema(description = "반대 수", example = "80", requiredMode = RequiredMode.REQUIRED)
        long disagreeCount,

        @Schema(description = "총 투표 수", example = "200", requiredMode = RequiredMode.REQUIRED)
        long totalVoteCount,

        @Schema(description = "찬성 비율 (0~1)", example = "0.60", requiredMode = RequiredMode.REQUIRED)
        double agreeRatio,

        @Schema(description = "반대 비율 (0~1)", example = "0.40", requiredMode = RequiredMode.REQUIRED)
        double disagreeRatio,

        @Schema(description = "연령대별 투표 참여 비율", requiredMode = RequiredMode.REQUIRED)
        List<VoteDemographicBreakdownResponse> ageBandBreakdown,

        @Schema(description = "성별 투표 참여 비율", requiredMode = RequiredMode.REQUIRED)
        List<VoteDemographicBreakdownResponse> genderBreakdown
) {
    public static BillVoteDetailResponse from(BillVoteDetailResult result) {
        return new BillVoteDetailResponse(
                result.agreeCount(),
                result.disagreeCount(),
                result.totalVoteCount(),
                result.agreeRatio(),
                result.disagreeRatio(),
                result.ageBandBreakdown().stream().map(VoteDemographicBreakdownResponse::from).toList(),
                result.genderBreakdown().stream().map(VoteDemographicBreakdownResponse::from).toList()
        );
    }
}
