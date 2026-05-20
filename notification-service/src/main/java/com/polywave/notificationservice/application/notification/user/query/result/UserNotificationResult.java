package com.polywave.notificationservice.application.notification.user.query.result;

import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.UserNotification;
import java.time.Instant;

public record UserNotificationResult(
        Long notificationId,
        NotificationCategory category,
        String categoryName,
        NotificationChannel channel,
        String title,
        String body,
        Instant sentAt,
        boolean isRead,
        LandingType landingType,
        Long landingId,
        String landingUrl
) {
    public static UserNotificationResult from(UserNotification n) {
        return new UserNotificationResult(
                n.getId(),
                n.getCategory(),
                n.getCategory() == null ? "" : n.getCategory().displayName(),
                n.getChannel(),
                n.getTitle(),
                n.getBody(),
                n.getSentAt(),
                n.isRead(),
                n.getLandingType(),
                n.getLandingId(),
                n.getLandingUrl()
        );
    }
}
