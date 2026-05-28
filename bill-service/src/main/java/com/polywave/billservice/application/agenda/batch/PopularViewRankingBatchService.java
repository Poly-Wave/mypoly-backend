package com.polywave.billservice.application.agenda.batch;

import com.polywave.billservice.domain.BillPopularViewRanking;
import com.polywave.billservice.domain.BillPopularViewRankingId;
import com.polywave.billservice.repository.command.PopularViewRankingCommandRepository;
import com.polywave.billservice.repository.query.PopularViewRankingQueryRepository;
import com.polywave.billservice.repository.query.PopularViewRankingQueryRepository.WeeklyViewIncreaseRow;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularViewRankingBatchService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int TOP_SIZE_LIMIT = 5;

    private final PopularViewRankingCommandRepository popularViewRankingCommandRepository;
    private final PopularViewRankingQueryRepository popularViewRankingQueryRepository;

    @Scheduled(cron = "${bill.agenda.popular-view.baseline-cron:0 0 0 * * MON}", zone = "Asia/Seoul")
    @Transactional
    public void refreshWeeklyBaseline() {
        LocalDate weekStart = currentWeekStartKst();
        refreshBaseline(weekStart);
        cleanupOldWeeks(weekStart);
    }

    @Scheduled(fixedDelayString = "${bill.agenda.popular-view.batch-fixed-delay-ms:600000}")
    @Transactional
    public void run() {
        LocalDate weekStart = currentWeekStartKst();
        Map<Long, Short> previousRankByBillId = popularViewRankingCommandRepository.findBillRankMapByWeekStart(weekStart);
        if (previousRankByBillId == null) {
            previousRankByBillId = Map.of();
        }

        if (!popularViewRankingCommandRepository.existsBaselineForWeek(weekStart)) {
            log.info("Baseline missing for current week; creating from current view_count: weekStart={}", weekStart);
            refreshBaseline(weekStart);
        }

        popularViewRankingCommandRepository.deleteRankingByWeekStart(weekStart);

        List<WeeklyViewIncreaseRow> topRows =
                popularViewRankingQueryRepository.findTopWeeklyViewIncreases(weekStart, TOP_SIZE_LIMIT);
        if (topRows.isEmpty()) {
            log.info("Popular view ranking empty: weekStart={}", weekStart);
            cleanupOldWeeks(weekStart);
            return;
        }

        Instant calculatedAt = Instant.now();
        List<BillPopularViewRanking> rankings = new ArrayList<>(topRows.size());
        short rank = 1;
        for (WeeklyViewIncreaseRow row : topRows) {
            rankings.add(new BillPopularViewRanking(
                    new BillPopularViewRankingId(weekStart, rank),
                    row.billId(),
                    row.viewCountWeekly(),
                    previousRankByBillId.get(row.billId()),
                    calculatedAt));
            rank++;
        }
        popularViewRankingCommandRepository.saveRankings(rankings);
        cleanupOldWeeks(weekStart);
        log.info("Popular view ranking updated: weekStart={}, count={}", weekStart, rankings.size());
    }

    private void refreshBaseline(LocalDate weekStart) {
        popularViewRankingCommandRepository.deleteBaselineByWeekStart(weekStart);
        popularViewRankingCommandRepository.insertBaselineForAllBills(weekStart);
        log.info("Popular view week baseline refreshed: weekStart={}", weekStart);
    }

    private void cleanupOldWeeks(LocalDate weekStart) {
        popularViewRankingCommandRepository.deleteBaselineOlderThan(weekStart);
        popularViewRankingCommandRepository.deleteRankingOlderThan(weekStart);
    }

    static LocalDate weekStartOf(LocalDate dateKst) {
        return dateKst.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate currentWeekStartKst() {
        return weekStartOf(LocalDate.now(KST));
    }
}
