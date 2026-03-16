package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "안건 한 건 응답")
public record AgendaResponse(
        @Schema(description = "의안 ID", example = "1")
        Long billId,

        @Schema(description = "의안 공식 제목", example = "○○법 일부개정법률안")
        String officialTitle,

        @Schema(description = "제안일", example = "2024-01-15")
        LocalDate proposalDate,

        @Schema(description = "상세 URL", example = "https://...")
        String detailUrl,

        @Schema(description = "찬성 비율 (0~1)", example = "0.52")
        double agreeRatio,

        @Schema(description = "반대 비율 (0~1)", example = "0.48")
        double disagreeRatio,

        @Schema(description = "총 투표 수", example = "200")
        long totalVoteCount
) {
    public static AgendaResponse from(AgendaResult result) {
        return new AgendaResponse(
                result.billId(),
                result.officialTitle(),
                result.proposalDate(),
                result.detailUrl(),
                result.agreeRatio(),
                result.disagreeRatio(),
                result.totalVoteCount()
        );
    }
}
