package com.polywave.billservice.application.vote.query.result;

import java.time.Instant;
import java.time.LocalDate;

public record MyVotedBillResult(
        Long billId,
        String title,
        String headline,
        LocalDate registeredDate,
        Instant votedAt,
        String voteResult,
        Integer currentProcStageOrder,
        String categoryCode,
        String categoryName,
        String categoryBackgroundColor,
        long viewCount,
        long voteCount
) {
}
