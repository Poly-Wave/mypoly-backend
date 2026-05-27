package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.domain.notification.UserNotification;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 알림 강제 발급 응답")
public record DevDeliverNotificationResponse(
        @Schema(description = "신규 발급되었으면 true, dedupKey 충돌 또는 정책 비활성으로 skip 되었으면 false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean delivered,

        @Schema(description = "신규 발급된 알림 ID(skip 시 null)")
        Long notificationId
) {
    public static DevDeliverNotificationResponse delivered(UserNotification n) {
        return new DevDeliverNotificationResponse(true, n.getId());
    }

    public static DevDeliverNotificationResponse skipped() {
        return new DevDeliverNotificationResponse(false, null);
    }
}
