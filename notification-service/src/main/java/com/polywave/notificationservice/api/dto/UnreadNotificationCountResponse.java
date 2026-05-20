package com.polywave.notificationservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "안 읽은 알림 개수 응답")
public record UnreadNotificationCountResponse(
        @Schema(description = "안 읽은 알림 개수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
        long unreadCount
) {
    public static UnreadNotificationCountResponse of(long unreadCount) {
        return new UnreadNotificationCountResponse(unreadCount);
    }
}
