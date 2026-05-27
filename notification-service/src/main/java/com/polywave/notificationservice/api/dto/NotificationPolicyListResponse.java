package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.policy.query.service.NotificationPolicyQueryService.PolicyPage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "알림 정책 목록 응답")
public record NotificationPolicyListResponse(
        @Schema(description = "정책 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<NotificationPolicyResponse> policies,

        @Schema(description = "현재 페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int page,

        @Schema(description = "페이지 크기", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext
) {
    public static NotificationPolicyListResponse from(PolicyPage page) {
        return new NotificationPolicyListResponse(
                page.content().stream()
                        .map(NotificationPolicyResponse::from)
                        .toList(),
                page.page(),
                page.size(),
                page.hasNext()
        );
    }
}
