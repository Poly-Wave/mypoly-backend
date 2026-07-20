package com.polywave.billservice.application.agenda.query.result;

import java.time.LocalDate;

public record PopularAgendaResult(
        int rank,
        Long billId,
        String officialTitle,
        String headline,
        String categoryCode,
        String categoryName,
        String categoryTextColor,
        LocalDate proposalDate,
        long viewCount,
        long viewCountWeekly,
        Short previousRank,
        long voteCount,
        boolean hasVoted) {
}
