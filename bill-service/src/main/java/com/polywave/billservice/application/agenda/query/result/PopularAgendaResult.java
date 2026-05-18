package com.polywave.billservice.application.agenda.query.result;

import java.time.LocalDate;

public record PopularAgendaResult(
        int rank,
        Long billId,
        String officialTitle,
        String categoryCode,
        String categoryName,
        LocalDate proposalDate,
        long viewCount,
        long viewCountWeekly,
        long voteCount,
        boolean hasVoted) {
}
