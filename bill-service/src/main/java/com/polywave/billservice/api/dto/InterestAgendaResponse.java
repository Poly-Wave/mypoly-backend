package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "관심 주제 안건 목록 응답")
public record InterestAgendaResponse(
        @Schema(description = "주제 코드", example = "DIGITAL")
        String categoryCode,

        @Schema(description = "주제 이름", example = "디지털")
        String categoryName,

        @Schema(description = "주제 아이콘 URL", example = "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.webp")
        String categoryIconUrl,

        @Schema(description = "주제 배경색(HEX, # 제외)", example = "46D9E3")
        String categoryBackgroundColor,

        @Schema(description = "주제 텍스트색(HEX, # 제외)", example = "181B2A")
        String categoryTextColor,

        @Schema(
                description = "제목",
                example = "○○법 일부개정법률안",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(description = "내용 요약", example = "AI 분석 요약입니다.")
        String content,

        @Schema(
                description = "등록일자",
                example = "2026-04-16",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDate registeredDate
) {
    public static InterestAgendaResponse from(MainAgendaResult result) {
        return new InterestAgendaResponse(
                result.categoryCode(),
                result.categoryName(),
                result.categoryIconUrl(),
                result.categoryBackgroundColor(),
                result.categoryTextColor(),
                result.officialTitle(),
                result.summary(),
                result.proposalDate()
        );
    }
}
