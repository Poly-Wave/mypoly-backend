package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.service.BillUiStage;
import com.polywave.billservice.application.bookmark.query.result.BookmarkedBillResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "보관함 안건 목록 응답")
public record BookmarkedBillResponse(
        @Schema(description = "의안 ID", example = "1")
        Long billId,

        @Schema(description = "의안 제목", example = "소득세법 일부개정법률안")
        String title,

        @Schema(description = "의안 접수일", example = "2026-04-18")
        LocalDate registeredDate,

        @Schema(description = "보관한 시각", example = "2026-04-18T01:10:00+09:00")
        Instant bookmarkedAt,

        @Schema(description = "앱용 진행 단계 코드", example = "REVIEW")
        String stageCode,

        @Schema(description = "앱용 진행 단계명", example = "심사")
        String stageName,

        @Schema(description = "앱용 진행 단계 순서", example = "2")
        int stageOrder,

        @Schema(description = "대표 카테고리 코드", example = "DIGITAL")
        String categoryCode,

        @Schema(description = "대표 카테고리명", example = "디지털")
        String categoryName,

        @Schema(description = "대표 카테고리 배경색 HEX, # 제외", example = "46D9E3")
        String categoryBackgroundColor,

        @Schema(description = "조회수", example = "23")
        long viewCount,

        @Schema(description = "투표수", example = "999")
        long voteCount,

        @Schema(description = "현재 사용자의 보관 여부", example = "true")
        boolean bookmarked
) {
    public static BookmarkedBillResponse from(BookmarkedBillResult result) {
        BillUiStage stage = BillUiStage.fromProcStageOrder(result.currentProcStageOrder());

        return new BookmarkedBillResponse(
                result.billId(),
                nullToEmpty(result.title()),
                result.registeredDate(),
                result.bookmarkedAt(),
                stage.code(),
                stage.displayName(),
                stage.order(),
                nullToEmpty(result.categoryCode()),
                nullToEmpty(result.categoryName()),
                nullToEmpty(result.categoryBackgroundColor()),
                result.viewCount(),
                result.voteCount(),
                true
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}