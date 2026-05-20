package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.agenda.query.result.PopularAgendaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDate;

@Schema(description = "인기 안건(주간 조회 Top) 응답")
public record PopularAgendaResponse(
        @Schema(description = "순위 (1~5)", example = "1", requiredMode = RequiredMode.REQUIRED)
        int rank,

        @Schema(description = "의안 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "주제 코드", example = "DIGITAL", requiredMode = RequiredMode.REQUIRED)
        String categoryCode,

        @Schema(description = "주제 이름", example = "디지털", requiredMode = RequiredMode.REQUIRED)
        String categoryName,

        @Schema(description = "제목", example = "○○법 일부개정법률안", requiredMode = RequiredMode.REQUIRED)
        String title,

        @Schema(description = "등록일자", example = "2026-04-16", requiredMode = RequiredMode.REQUIRED)
        LocalDate registeredDate,

        @Schema(description = "누적 조회수", example = "120", requiredMode = RequiredMode.REQUIRED)
        long viewCount,

        @Schema(description = "이번 주 조회 증가분 (KST 월요일 00:00 기준)", example = "45", requiredMode = RequiredMode.REQUIRED)
        long viewCountWeekly,

        @Schema(description = "투표수", example = "123", requiredMode = RequiredMode.REQUIRED)
        long voteCount,

        @Schema(description = "현재 사용자 투표 여부", example = "false", requiredMode = RequiredMode.REQUIRED)
        boolean hasVoted) {

    public static PopularAgendaResponse from(PopularAgendaResult result) {
        return new PopularAgendaResponse(
                result.rank(),
                result.billId(),
                result.categoryCode(),
                result.categoryName(),
                result.officialTitle(),
                result.proposalDate(),
                result.viewCount(),
                result.viewCountWeekly(),
                result.voteCount(),
                result.hasVoted());
    }
}
