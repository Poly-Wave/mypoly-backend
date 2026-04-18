package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.service.BillUiStage;
import com.polywave.billservice.application.member.query.result.BillMemberRepresentativeBillResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "국회의원 상세 대표 발의 의안 응답")
public record BillMemberRepresentativeBillResponse(
        @Schema(description = "의안 ID", example = "495")
        Long billId,

        @Schema(description = "의안 제목", example = "공직선거법 일부개정법률안")
        String title,

        @Schema(description = "의안 발의일", example = "2026-03-19")
        LocalDate proposedDate,

        @Schema(description = "앱용 진행 단계 코드", example = "REVIEW")
        String stageCode,

        @Schema(description = "앱용 진행 단계명", example = "심사")
        String stageName,

        @Schema(description = "앱용 진행 단계 순서", example = "2")
        int stageOrder,

        @Schema(description = "대표 카테고리 코드", example = "POLITICS")
        String categoryCode,

        @Schema(description = "대표 카테고리명", example = "정치")
        String categoryName,

        @Schema(description = "대표 카테고리 배경색 HEX, # 제외", example = "FFECEC")
        String categoryBackgroundColor,

        @Schema(description = "조회수", example = "2")
        long viewCount,

        @Schema(description = "투표수", example = "11")
        long voteCount
) {

    public static BillMemberRepresentativeBillResponse from(BillMemberRepresentativeBillResult result) {
        BillUiStage stage = BillUiStage.fromProcStageOrder(result.currentProcStageOrder());

        return new BillMemberRepresentativeBillResponse(
                result.billId(),
                nullToEmpty(result.title()),
                result.proposedDate(),
                stage.code(),
                stage.displayName(),
                stage.order(),
                nullToEmpty(result.categoryCode()),
                nullToEmpty(result.categoryName()),
                nullToEmpty(result.categoryBackgroundColor()),
                result.viewCount(),
                result.voteCount()
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}