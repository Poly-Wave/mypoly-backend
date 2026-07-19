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

        @Schema(description = "주제 텍스트색(HEX, # 제외)", example = "503838", requiredMode = RequiredMode.REQUIRED)
        String categoryTextColor,

        @Schema(description = "제목", example = "○○법 일부개정법률안", requiredMode = RequiredMode.REQUIRED)
        String title,

        @Schema(description = "AI 헤드라인", example = "직장인 세부담 조정", requiredMode = RequiredMode.REQUIRED)
        String headline,

        @Schema(description = "등록일자", example = "2026-04-16", requiredMode = RequiredMode.REQUIRED)
        LocalDate registeredDate,

        @Schema(description = "누적 조회수", example = "120", requiredMode = RequiredMode.REQUIRED)
        long viewCount,

        @Schema(description = "이번 주 조회 증가분 (KST 월요일 00:00 기준)", example = "45", requiredMode = RequiredMode.REQUIRED)
        long viewCountWeekly,

        @Schema(description = "이전 배치 기준 순위. 이전 배치 데이터가 없으면 null", example = "3")
        Integer previousRank,

        @Schema(description = "이전 배치 대비 순위 변동 단계 수(상승: 양수, 하락: 음수, 유지/신규: 0)", example = "2", requiredMode = RequiredMode.REQUIRED)
        int rankChangeSteps,

        @Schema(
                description = "순위 변동 유형",
                example = "UP",
                requiredMode = RequiredMode.REQUIRED,
                implementation = RankChangeType.class)
        RankChangeType rankChangeType,

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
                result.categoryTextColor(),
                result.officialTitle(),
                nullToEmpty(result.headline()),
                result.proposalDate(),
                result.viewCount(),
                result.viewCountWeekly(),
                result.previousRank() == null ? null : result.previousRank().intValue(),
                calculateRankChangeSteps(result.rank(), result.previousRank()),
                calculateRankChangeType(result.rank(), result.previousRank()),
                result.voteCount(),
                result.hasVoted());
    }

    private static int calculateRankChangeSteps(int currentRank, Short previousRank) {
        if (previousRank == null) {
            return 0;
        }
        return previousRank - currentRank;
    }

    private static RankChangeType calculateRankChangeType(int currentRank, Short previousRank) {
        if (previousRank == null) {
            return RankChangeType.NEW;
        }
        if (previousRank == currentRank) {
            return RankChangeType.SAME;
        }
        return previousRank > currentRank ? RankChangeType.UP : RankChangeType.DOWN;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
