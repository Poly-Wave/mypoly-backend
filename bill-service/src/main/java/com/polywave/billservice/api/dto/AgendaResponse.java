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

        @Schema(description = "반대 비율 (0~1)", example = "0.48")
        double disagreeRatio,

        @Schema(description = "총 투표 수", example = "200")
        long totalVoteCount,

        @Schema(description = "현재 사용자의 투표 여부", example = "true")
        boolean hasVoted,

        @Schema(
                description = "메인 리스트 카테고리 이미지 URL",
                example = "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/ENVIRONMENT_main_64.webp",
                nullable = true
        )
        String iconUrl
) {
    private static final String ICON_PREFIX = "bill-categories";
    private static final String MAIN_ICON_SUFFIX = "_main_64.webp";

    public static AgendaResponse from(AgendaResult result, String iconBaseUrl) {
        double agreeRatio = result.agreeRatio();
        double disagreeRatio = result.disagreeRatio();

        // 소수점 2자리 반올림 시 합이 1.00이 되도록 찬성 비율을 먼저 반올림하고 반대 비율을 보정한다.
        if (result.totalVoteCount() > 0) {
            agreeRatio = roundTo2(agreeRatio);
            disagreeRatio = roundTo2(1.0 - agreeRatio);
        }

        String iconUrl = null;
        if (result.categoryCode() != null && !result.categoryCode().isBlank()) {
            iconUrl = iconBaseUrl + "/" + ICON_PREFIX + "/" + result.categoryCode() + MAIN_ICON_SUFFIX;
        }

        return new AgendaResponse(
                result.billId(),
                result.officialTitle(),
                agreeRatio,
                disagreeRatio,
                result.totalVoteCount(),
                result.hasVoted(),
                iconUrl
        );
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}