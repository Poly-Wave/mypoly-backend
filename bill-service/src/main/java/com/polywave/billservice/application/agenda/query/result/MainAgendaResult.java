package com.polywave.billservice.application.agenda.query.result;

import java.time.LocalDate;

public record MainAgendaResult(
        Long billId,
        String officialTitle,
        String headline,
        String summary,
        String categoryIconUrl,
        LocalDate proposalDate,
        long viewCount,
        long voteCount,
        String categoryCode,
        String categoryName,
        String categoryBackgroundColor,
        String categoryTextColor) {
}
