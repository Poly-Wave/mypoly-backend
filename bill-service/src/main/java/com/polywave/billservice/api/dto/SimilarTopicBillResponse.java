package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.SimilarTopicBillResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "유사 주제 의안 응답")
public record SimilarTopicBillResponse(
        @Schema(description = "의안 ID", example = "12")
        Long billId,

        @Schema(description = "의안 제목")
        String officialTitle,

        @Schema(description = "접수일", example = "2025-12-31")
        LocalDate proposalDate,

        @Schema(description = "AI 헤드라인")
        String headline,

        @Schema(description = "AI 요약")
        String summary,

        @Schema(
                description = "AI 3줄 요약. 각 줄이 배열 원소로 내려간다.",
                example = "[\"소득세 과세표준 구간을 조정합니다.\", \"중산층 세부담이 낮아집니다.\", \"내년 1월부터 적용됩니다.\"]"
        )
        List<String> summaryLines,

        @Schema(description = "원문 URL")
        String detailUrl,

        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "카테고리 코드", example = "ECONOMY")
        String categoryCode,

        @Schema(description = "카테고리명", example = "경제")
        String categoryName
) {
    public static SimilarTopicBillResponse from(SimilarTopicBillResult result) {
        return new SimilarTopicBillResponse(
                result.billId(),
                result.officialTitle(),
                result.proposalDate(),
                result.headline(),
                result.summary(),
                result.summaryLines(),
                result.detailUrl(),
                result.categoryId(),
                result.categoryCode(),
                result.categoryName()
        );
    }
}