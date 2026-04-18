package com.polywave.billservice.application.bookmark.query.result;

import java.time.Instant;
import java.time.LocalDate;

public record BookmarkedBillResult(
        Long billId,
        String title,
        LocalDate registeredDate,
        Instant bookmarkedAt,
        Integer currentProcStageOrder,
        String categoryCode,
        String categoryName,
        String categoryBackgroundColor,
        long viewCount,
        long voteCount
) {
}