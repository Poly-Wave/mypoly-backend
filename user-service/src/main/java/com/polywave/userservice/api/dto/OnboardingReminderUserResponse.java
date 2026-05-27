package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.user.query.result.OnboardingReminderUserResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[Internal] 온보딩 리마인더 발급 대상 유저")
public record OnboardingReminderUserResponse(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,

        @Schema(description = "닉네임(템플릿 변수 치환용, null 이면 빈 문자열)", example = "당근도사", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname
) {
    public static OnboardingReminderUserResponse from(OnboardingReminderUserResult r) {
        return new OnboardingReminderUserResponse(r.userId(), r.nickname() == null ? "" : r.nickname());
    }
}
