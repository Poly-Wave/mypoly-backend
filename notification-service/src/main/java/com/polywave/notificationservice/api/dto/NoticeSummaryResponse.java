package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notice.query.result.NoticeSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Schema(description = "공지사항 목록 단건 응답")
public record NoticeSummaryResponse(
        @Schema(description = "공지사항 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long noticeId,

        @Schema(description = "제목", example = "[공지] 서비스 점검 안내", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "등록일(KST, YYYY.MM.DD)", example = "2026.12.30", requiredMode = Schema.RequiredMode.REQUIRED)
        String displayDate
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static NoticeSummaryResponse from(NoticeSummaryResult result) {
        return new NoticeSummaryResponse(
                result.id(),
                result.title(),
                toDisplayDate(result.createdAt())
        );
    }

    private static String toDisplayDate(Instant createdAt) {
        return createdAt == null ? "" : LocalDate.ofInstant(createdAt, KST).format(DISPLAY_DATE);
    }
}
