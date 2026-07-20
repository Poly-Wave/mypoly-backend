package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.service.BillUiStage;
import com.polywave.billservice.application.vote.query.result.MyVotedBillResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "참여한 투표 안건 목록 응답")
public record MyVotedBillResponse(
        @Schema(description = "의안 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "의안 제목", example = "소득세법 일부개정법률안", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "AI 헤드라인", example = "직장인 세부담 조정", requiredMode = Schema.RequiredMode.REQUIRED)
        String headline,

        @Schema(description = "의안 접수일", example = "2026-04-18", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate registeredDate,

        @Schema(description = "투표한 시각(KST, +09:00 오프셋 포함)", example = "2026-04-18T15:20:40.245724+09:00", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime votedAt,

        @Schema(description = "현재 사용자의 투표 결과", example = "AGREE", allowableValues = {"AGREE", "DISAGREE"}, requiredMode = Schema.RequiredMode.REQUIRED)
        String myVoteResult,

        @Schema(description = "앱용 진행 단계 코드", example = "REVIEW", requiredMode = Schema.RequiredMode.REQUIRED)
        String stageCode,

        @Schema(description = "앱용 진행 단계명", example = "심사", requiredMode = Schema.RequiredMode.REQUIRED)
        String stageName,

        @Schema(description = "앱용 진행 단계 순서", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        int stageOrder,

        @Schema(description = "대표 카테고리 코드", example = "DIGITAL", requiredMode = Schema.RequiredMode.REQUIRED)
        String categoryCode,

        @Schema(description = "대표 카테고리명", example = "디지털", requiredMode = Schema.RequiredMode.REQUIRED)
        String categoryName,

        @Schema(description = "대표 카테고리 배경색 HEX, # 제외", example = "46D9E3", requiredMode = Schema.RequiredMode.REQUIRED)
        String categoryBackgroundColor,

        @Schema(description = "조회수", example = "23", requiredMode = Schema.RequiredMode.REQUIRED)
        long viewCount,

        @Schema(description = "투표수", example = "999", requiredMode = Schema.RequiredMode.REQUIRED)
        long voteCount
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static MyVotedBillResponse from(MyVotedBillResult result) {
        BillUiStage stage = BillUiStage.fromProcStageOrder(result.currentProcStageOrder());

        return new MyVotedBillResponse(
                result.billId(),
                nullToEmpty(result.title()),
                nullToEmpty(result.headline()),
                result.registeredDate(),
                toKstOffsetDateTime(result.votedAt()),
                nullToEmpty(result.voteResult()),
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

    private static OffsetDateTime toKstOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(KST).toOffsetDateTime();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}