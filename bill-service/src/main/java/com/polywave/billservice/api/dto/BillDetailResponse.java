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
        int proposerCount,

        @Schema(description = "원문 URL")
        String detailUrl,

        @Schema(description = "조회수", example = "0")
        long viewCount,

        @Schema(description = "현재 사용자의 보관 여부", example = "true")
        boolean bookmarked,

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
            BillVoteSummaryResult voteSummary,
            boolean bookmarked
    ) {
        return new BillDetailResponse(
                detail.billId(),
                nullToEmpty(detail.officialTitle()),
                detail.proposalDate(),
                nullToEmpty(detail.representativeProposerName()),
                detail.proposerCount() == null ? 0 : detail.proposerCount(),
                nullToEmpty(detail.detailUrl()),
                detail.viewCount() == null ? 0L : detail.viewCount(),
                bookmarked,
                BillStageResponse.from(detail),
                BillAiSummaryResponse.from(detail),
                categories == null ? List.of() : categories.stream().map(BillCategorySummaryResponse::from).toList(),
                BillVoteSummaryResponse.from(voteSummary)
        );
    }

    public BillDetailResponse withViewCount(long viewCount) {
        return new BillDetailResponse(
                billId,
                officialTitle,
                proposalDate,
                representativeProposerName,
                proposerCount,
                detailUrl,
                viewCount,
                bookmarked,
                stage,
                aiSummary,
                categories,
                voteSummary
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}