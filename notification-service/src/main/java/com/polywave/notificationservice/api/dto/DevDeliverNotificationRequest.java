package com.polywave.notificationservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "[DEV] 알림 강제 발급 요청")
public record DevDeliverNotificationRequest(
        @Schema(description = "수신자 user_id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long userId,

        @Schema(description = "정책 ID(ACTIVE 상태여야 발급됨)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long policyId,

        @Schema(description = "랜딩 대상 ID(상세 화면 이동 시 사용)", example = "10")
        Long landingId,

        @Schema(description = "중복 방지 키. 동일 키로 재호출 시 멱등 skip", example = "DEV_TEST_DELIVER_2026_05_20")
        String dedupKey
) {
}
