package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.user.query.service.UserNotificationQueryService.MyNotificationPage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "앱 알림함 목록 응답")
public record MyNotificationListResponse(
        @Schema(description = "알림 목록(없으면 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MyNotificationResponse> notifications,

        @Schema(description = "현재 페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int page,

        @Schema(description = "페이지 크기", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext
) {
    public static MyNotificationListResponse from(MyNotificationPage page) {
        return new MyNotificationListResponse(
                page.content().stream()
                        .map(MyNotificationResponse::from)
                        .toList(),
                page.page(),
                page.size(),
                page.hasNext()
        );
    }
}
