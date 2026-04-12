package com.polywave.billservice.application.bill.query.result;

import java.time.LocalDate;

public record SimilarTopicBillResult(
        Long billId,
        String officialTitle,
        LocalDate proposalDate,
        String headline,
        String summary,
        String detailUrl,
        Long categoryId,
        String categoryCode,
        String categoryName
) {
}