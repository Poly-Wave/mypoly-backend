package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.BillPopularViewRanking;
import java.time.LocalDate;
import java.util.List;

public interface PopularViewRankingCommandRepository {

    boolean existsBaselineForWeek(LocalDate weekStart);

    void deleteBaselineByWeekStart(LocalDate weekStart);

    void deleteBaselineOlderThan(LocalDate weekStart);

    void insertBaselineForAllBills(LocalDate weekStart);

    void deleteRankingByWeekStart(LocalDate weekStart);

    void deleteRankingOlderThan(LocalDate weekStart);

    void saveRankings(List<BillPopularViewRanking> rankings);
}
