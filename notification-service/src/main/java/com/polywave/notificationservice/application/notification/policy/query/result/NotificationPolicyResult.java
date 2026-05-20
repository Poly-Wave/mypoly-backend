package com.polywave.notificationservice.application.notification.policy.query.result;

import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import java.time.Instant;

public record NotificationPolicyResult(
        Long policyId,
        String policyKey,
        String name,
        String depth,
        NotificationChannel channel,
        NotificationCategory category,
        String targetAudience,
        String sendSchedule,
        String title,
        String body,
        LandingType landingType,
        String landingUrl,
        NotificationPolicyStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static NotificationPolicyResult from(NotificationPolicy policy) {
        return new NotificationPolicyResult(
                policy.getId(),
                policy.getPolicyKey(),
                policy.getName(),
                policy.getDepth(),
                policy.getChannel(),
                policy.getCategory(),
                policy.getTargetAudience(),
                policy.getSendSchedule(),
                policy.getTitle(),
                policy.getBody(),
                policy.getLandingType(),
                policy.getLandingUrl(),
                policy.getStatus(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }
}
