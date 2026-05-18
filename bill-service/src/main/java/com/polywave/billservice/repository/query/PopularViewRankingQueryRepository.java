package com.polywave.billservice.repository.query;

import java.time.LocalDate;
import java.util.List;

public interface PopularViewRankingQueryRepository {

    List<WeeklyViewIncreaseRow> findTopWeeklyViewIncreases(LocalDate weekStart, int limit);

    record WeeklyViewIncreaseRow(Long billId, Long viewCountWeekly) {
    }
}
