package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "AI 요약 응답")
public record BillAiSummaryResponse(
        @Schema(description = "AI 헤드라인", example = "직장인 세부담 조정", requiredMode = RequiredMode.REQUIRED)
        String headline,

        @Schema(
                description = "AI 3줄 요약. 각 줄이 배열 원소로 내려간다.",
                example = "[\"소득세 과세표준 구간을 조정합니다.\", \"중산층 세부담이 낮아집니다.\", \"내년 1월부터 적용됩니다.\"]",
                requiredMode = RequiredMode.REQUIRED
        )
        List<String> summaryLines
) {
    public static BillAiSummaryResponse from(BillDetailResult detail) {
        return new BillAiSummaryResponse(
                nullToEmpty(detail.aiHeadline()),
                toSummaryLines(detail)
        );
    }

    private static List<String> toSummaryLines(BillDetailResult detail) {
        List<String> lines = new ArrayList<>();
        addIfPresent(lines, detail.aiSummary1());
        addIfPresent(lines, detail.aiSummary2());
        addIfPresent(lines, detail.aiSummary3());
        return lines;
    }

    private static void addIfPresent(List<String> lines, String value) {
        if (value != null && !value.isBlank()) {
            lines.add(value.strip());
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
