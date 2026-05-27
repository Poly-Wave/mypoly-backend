package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.policy.query.result.NotificationPolicyResult;
import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "알림 정책 응답")
public record NotificationPolicyResponse(
        @Schema(description = "정책 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long policyId,

        @Schema(description = "정책 식별 키", example = "ONBOARDING_INTEREST_REMIND_D1")
        String policyKey,

        @Schema(description = "정책 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "노출 위치")
        String depth,

        @Schema(description = "알림 채널", requiredMode = Schema.RequiredMode.REQUIRED)
        NotificationChannel channel,

        @Schema(description = "알림 카테고리", requiredMode = Schema.RequiredMode.REQUIRED)
        NotificationCategory category,

        @Schema(description = "발송 대상")
        String targetAudience,

        @Schema(description = "발송 시점")
        String sendSchedule,

        @Schema(description = "메시지 타이틀")
        String title,

        @Schema(description = "메시지 본문", requiredMode = Schema.RequiredMode.REQUIRED)
        String body,

        @Schema(description = "랜딩 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        LandingType landingType,

        @Schema(description = "외부 URL")
        String landingUrl,

        @Schema(description = "운영 상태", requiredMode = Schema.RequiredMode.REQUIRED)
        NotificationPolicyStatus status,

        @Schema(description = "생성 시각(KST 오프셋)", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime createdAt,

        @Schema(description = "수정 시각(KST 오프셋)", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime updatedAt
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static NotificationPolicyResponse from(NotificationPolicyResult r) {
        return new NotificationPolicyResponse(
                r.policyId(),
                r.policyKey(),
                r.name(),
                r.depth(),
                r.channel(),
                r.category(),
                r.targetAudience(),
                r.sendSchedule(),
                r.title(),
                r.body(),
                r.landingType(),
                r.landingUrl(),
                r.status(),
                toKst(r.createdAt()),
                toKst(r.updatedAt())
        );
    }

    private static OffsetDateTime toKst(Instant instant) {
        return instant == null ? null : instant.atZone(KST).toOffsetDateTime();
    }
}
