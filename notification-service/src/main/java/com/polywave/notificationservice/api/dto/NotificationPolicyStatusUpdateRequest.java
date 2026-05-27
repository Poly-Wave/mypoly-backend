package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알림 정책 상태 변경 요청")
public record NotificationPolicyStatusUpdateRequest(
        @Schema(description = "변경할 상태", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"READY", "ACTIVE", "INACTIVE"})
        @NotNull
        NotificationPolicyStatus status
) {
}
