package com.polywave.billservice.application.agenda.query.result;

import com.polywave.billservice.common.SummaryLineJoiner;
import java.time.LocalDate;
import java.util.List;

public record MainAgendaResult(
        Long billId,
        String officialTitle,
        String summary1,
        String summary2,
        String summary3,
        String categoryIconUrl,
        LocalDate proposalDate,
        long viewCount,
        long voteCount,
        String categoryCode,
        String categoryName,
        String categoryBackgroundColor,
        String categoryTextColor) {
    public String summary() {
        return SummaryLineJoiner.join(summary1, summary2, summary3);
    }

    public List<String> summaryLines() {
        return SummaryLineJoiner.toLines(summary1, summary2, summary3);
    }
}
