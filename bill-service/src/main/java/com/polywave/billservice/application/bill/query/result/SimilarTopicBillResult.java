package com.polywave.billservice.application.bill.query.result;

import com.polywave.billservice.common.SummaryLineJoiner;
import java.time.LocalDate;
import java.util.List;

public record SimilarTopicBillResult(
        Long billId,
        String officialTitle,
        LocalDate proposalDate,
        String headline,
        String summary1,
        String summary2,
        String summary3,
        String detailUrl,
        Long categoryId,
        String categoryCode,
        String categoryName
) {
    public String summary() {
        return SummaryLineJoiner.join(summary1, summary2, summary3);
    }

    public List<String> summaryLines() {
        return SummaryLineJoiner.toLines(summary1, summary2, summary3);
    }

}