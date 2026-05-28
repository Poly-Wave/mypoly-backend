package com.polywave.billservice.repository.command.impl;

import com.polywave.billservice.domain.BillPopularViewRanking;
import com.polywave.billservice.domain.QBillPopularViewRanking;
import com.polywave.billservice.domain.QBillViewWeekBaseline;
import com.polywave.billservice.repository.command.PopularViewRankingCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularViewRankingCommandRepositoryImpl implements PopularViewRankingCommandRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public boolean existsBaselineForWeek(LocalDate weekStart) {
        QBillViewWeekBaseline baseline = QBillViewWeekBaseline.billViewWeekBaseline;
        return queryFactory
                .selectOne()
                .from(baseline)
                .where(baseline.id.weekStart.eq(weekStart))
                .fetchFirst() != null;
    }

    @Override
    public void deleteBaselineByWeekStart(LocalDate weekStart) {
        QBillViewWeekBaseline baseline = QBillViewWeekBaseline.billViewWeekBaseline;
        queryFactory.delete(baseline)
                .where(baseline.id.weekStart.eq(weekStart))
                .execute();
    }

    @Override
    public void deleteBaselineOlderThan(LocalDate weekStart) {
        QBillViewWeekBaseline baseline = QBillViewWeekBaseline.billViewWeekBaseline;
        queryFactory.delete(baseline)
                .where(baseline.id.weekStart.lt(weekStart))
                .execute();
    }

    @Override
    public void insertBaselineForAllBills(LocalDate weekStart) {
        entityManager.createNativeQuery("""
                        INSERT INTO bill_service.bill_view_week_baseline (week_start, bill_id, baseline_view_count)
                        SELECT :weekStart, id, view_count
                        FROM bill_service.bills
                        """)
                .setParameter("weekStart", weekStart)
                .executeUpdate();
    }

    @Override
    public void deleteRankingByWeekStart(LocalDate weekStart) {
        QBillPopularViewRanking ranking = QBillPopularViewRanking.billPopularViewRanking;
        queryFactory.delete(ranking)
                .where(ranking.id.weekStart.eq(weekStart))
                .execute();
    }

    @Override
    public void deleteRankingOlderThan(LocalDate weekStart) {
        QBillPopularViewRanking ranking = QBillPopularViewRanking.billPopularViewRanking;
        queryFactory.delete(ranking)
                .where(ranking.id.weekStart.lt(weekStart))
                .execute();
    }

    @Override
    public Map<Long, Short> findBillRankMapByWeekStart(LocalDate weekStart) {
        QBillPopularViewRanking ranking = QBillPopularViewRanking.billPopularViewRanking;
        return queryFactory
                .select(ranking.billId, ranking.id.rank)
                .from(ranking)
                .where(ranking.id.weekStart.eq(weekStart))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(ranking.billId),
                        tuple -> tuple.get(ranking.id.rank)));
    }

    @Override
    public void saveRankings(List<BillPopularViewRanking> rankings) {
        for (BillPopularViewRanking ranking : rankings) {
            entityManager.persist(ranking);
        }
    }
}
