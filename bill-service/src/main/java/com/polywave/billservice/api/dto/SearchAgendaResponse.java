package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDate;

@Schema(description = "의안 검색 결과 응답")
public record SearchAgendaResponse(
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

        @Schema(description = "조회수", example = "0", requiredMode = RequiredMode.REQUIRED)
        long viewCount,

        @Schema(description = "투표수", example = "123", requiredMode = RequiredMode.REQUIRED)
        long voteCount) {

    public static SearchAgendaResponse from(SearchAgendaResult result) {
        return new SearchAgendaResponse(
                result.billId(),
                result.categoryCode(),
                result.categoryName(),
                result.officialTitle(),
                result.proposalDate(),
                result.viewCount(),
                result.voteCount());
    }
}
