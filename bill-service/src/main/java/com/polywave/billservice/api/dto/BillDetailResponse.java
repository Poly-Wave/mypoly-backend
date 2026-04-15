package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "의안 상세 응답")
public record BillDetailResponse(
        @Schema(description = "의안 ID", example = "1")
        Long billId,

        @Schema(description = "의안 공식 제목", example = "소득세법 일부개정법률안")
        String officialTitle,

        @Schema(description = "접수일", example = "2025-12-31")
        LocalDate proposalDate,

        @Schema(description = "대표 제안자명", example = "홍길동")
        String representativeProposerName,

        @Schema(description = "제안자 수", example = "10")
        Integer proposerCount,

        @Schema(description = "원문 URL")
        String detailUrl,

        @Schema(description = "진행 단계 정보")
        BillStageResponse stage,

        @Schema(description = "AI 요약 정보")
        BillAiSummaryResponse aiSummary,

        @Schema(description = "카테고리 목록")
        List<BillCategorySummaryResponse> categories,

        @Schema(description = "투표 요약")
        BillVoteSummaryResponse voteSummary
) {
    public static BillDetailResponse from(
            BillDetailResult detail,
            List<BillCategoryResult> categories,
            BillVoteSummaryResult voteSummary
    ) {
        return new BillDetailResponse(
                detail.billId(),
                detail.officialTitle(),
                detail.proposalDate(),
                detail.representativeProposerName(),
                detail.proposerCount(),
                detail.detailUrl(),
                BillStageResponse.from(detail),
                BillAiSummaryResponse.from(detail),
                categories.stream().map(BillCategorySummaryResponse::from).toList(),
                BillVoteSummaryResponse.from(voteSummary)
        );
    }
}