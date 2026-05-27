package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.scheduler.OnboardingReminderScheduler.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 온보딩 리마인더 스케줄러 수동 실행 응답")
public record RunOnboardingRemindResponse(
        @Schema(description = "행 1(별명 설정 D+1) 발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int nicknameRemindSent,

        @Schema(description = "행 2(관심 주제 선택 D+1) 발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int categoryRemindSent
) {
    public static RunOnboardingRemindResponse from(Result r) {
        return new RunOnboardingRemindResponse(r.nicknameRemindSent(), r.categoryRemindSent());
    }
}
