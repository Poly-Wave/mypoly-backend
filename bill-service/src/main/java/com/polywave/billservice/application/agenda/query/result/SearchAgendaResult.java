package com.polywave.billservice.application.agenda.query.result;

import java.time.LocalDate;

public record SearchAgendaResult(
        Long billId,
        String officialTitle,
        String headline,
        LocalDate proposalDate,
        long viewCount,
        long voteCount,
        String categoryCode,
        String categoryName,
        String categoryTextColor) {
}
