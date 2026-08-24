package com.polywave.notificationservice.application.notice.query.result;

import java.time.Instant;

public record NoticeSummaryResult(
        Long id,
        String title,
        Instant createdAt
) {
}
