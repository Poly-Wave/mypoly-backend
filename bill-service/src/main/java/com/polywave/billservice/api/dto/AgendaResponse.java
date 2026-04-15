package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Schema(description = "안건 한 건 응답")
public record AgendaResponse(
        @Schema(description = "의안 ID", example = "1")
        Long billId,

        @Schema(description = "의안 공식 제목", example = "○○법 일부개정법률안")
        String officialTitle,

        @Schema(description = "찬성 비율 (0~1)", example = "0.52")
        double agreeRatio,

        @Schema(description = "총 투표 수", example = "200")
        long totalVoteCount,

        @Schema(description = "현재 사용자의 투표 여부", example = "true")
        boolean hasVoted
) {
    public static AgendaResponse from(AgendaResult result) {
        double agreeRatio = result.agreeRatio();

        if (result.totalVoteCount() > 0) {
            agreeRatio = roundTo2(agreeRatio);
        }

        return new AgendaResponse(
                result.billId(),
                result.officialTitle(),
                agreeRatio,
                result.totalVoteCount(),
                result.hasVoted()
        );
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}