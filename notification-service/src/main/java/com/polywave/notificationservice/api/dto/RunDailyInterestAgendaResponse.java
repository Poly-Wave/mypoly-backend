package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.scheduler.DailyInterestAgendaScheduler.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 행 3 (관심 카테고리 신규 안건) 스케줄러 수동 실행 응답")
public record RunDailyInterestAgendaResponse(
        @Schema(description = "발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int sent
) {
    public static RunDailyInterestAgendaResponse from(Result r) {
        return new RunDailyInterestAgendaResponse(r.sent());
    }
}
