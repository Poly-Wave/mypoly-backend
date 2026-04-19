package com.polywave.billservice.application.member.query.result;

import java.time.LocalDate;

public record BillMemberRepresentativeBillResult(
        Long billId,
        String title,
        LocalDate proposedDate,
        Integer currentProcStageOrder,
        String categoryCode,
        String categoryName,
        String categoryBackgroundColor,
        long viewCount,
        long voteCount
) {
}
