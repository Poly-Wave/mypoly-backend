package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Schema(description = "의안 상세 응답")
public record BillDetailResponse(
        @Schema(description = "의안 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "의안 공식 제목", example = "소득세법 일부개정법률안", requiredMode = RequiredMode.REQUIRED)
        String officialTitle,

        @Schema(description = "접수일", example = "2025-12-31", requiredMode = RequiredMode.REQUIRED)
        LocalDate proposalDate,

        @Schema(description = "대표 제안자명", example = "홍길동", requiredMode = RequiredMode.REQUIRED)
        String representativeProposerName,

        @Schema(description = "제안자 수", example = "10", requiredMode = RequiredMode.REQUIRED)
        int proposerCount,

        @Schema(description = "원문 URL", requiredMode = RequiredMode.REQUIRED)
        String detailUrl,

        @Schema(description = "조회수", example = "0", requiredMode = RequiredMode.REQUIRED)
        long viewCount,

        @Schema(description = "현재 사용자의 보관 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
        boolean bookmarked,

        @Schema(description = "진행 단계 정보", requiredMode = RequiredMode.REQUIRED)
        BillStageResponse stage,

        @Schema(description = "AI 요약 정보", requiredMode = RequiredMode.REQUIRED)
        BillAiSummaryResponse aiSummary,

        @Schema(
                description = "대표 카테고리 정보. AI 카테고리 rankOrder 기준 첫 번째 카테고리입니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        BillCategorySummaryResponse category,

        @Schema(description = "투표 요약", requiredMode = RequiredMode.REQUIRED)
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
                toPrimaryCategory(categories),
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
                category,
                voteSummary
        );
    }

    private static BillCategorySummaryResponse toPrimaryCategory(List<BillCategoryResult> categories) {
        if (categories == null || categories.isEmpty()) {
            return BillCategorySummaryResponse.empty();
        }

        return categories.stream()
                .min(Comparator
                        .comparing(BillCategoryResult::rankOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(BillCategoryResult::categoryId, Comparator.nullsLast(Long::compareTo)))
                .map(BillCategorySummaryResponse::from)
                .orElseGet(BillCategorySummaryResponse::empty);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}