package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.scheduler.DailyHomeBroadcastScheduler.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 매일 홈 broadcast 수동 실행 응답")
public record RunDailyHomeBroadcastResponse(
        @Schema(description = "행 4(요즘 핫한 안건) 발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int trendingSent,

        @Schema(description = "행 5(최근 30일 인기 안건) 발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int recent30dPopularSent
) {
    public static RunDailyHomeBroadcastResponse from(Result r) {
        return new RunDailyHomeBroadcastResponse(r.trendingSent(), r.recent30dPopularSent());
    }
}
