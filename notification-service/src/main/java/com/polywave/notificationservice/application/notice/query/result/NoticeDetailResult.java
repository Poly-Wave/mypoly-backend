package com.polywave.notificationservice.application.notice.query.result;

import java.time.Instant;

public record NoticeDetailResult(
        Long id,
        String title,
        String content,
        Instant createdAt
) {
}
