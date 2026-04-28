package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "AI 요약 응답")
public record BillAiSummaryResponse(
        @Schema(description = "AI 헤드라인", example = "직장인 세부담 조정", requiredMode = RequiredMode.REQUIRED)
        String headline,

        @Schema(description = "AI 요약 본문", requiredMode = RequiredMode.REQUIRED)
        String summary
) {
    public static BillAiSummaryResponse from(BillDetailResult detail) {
        return new BillAiSummaryResponse(
                nullToEmpty(detail.aiHeadline()),
                nullToEmpty(detail.aiSummary())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}