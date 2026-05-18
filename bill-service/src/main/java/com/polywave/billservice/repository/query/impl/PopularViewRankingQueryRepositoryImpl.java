package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillViewWeekBaseline;
import com.polywave.billservice.repository.query.PopularViewRankingQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularViewRankingQueryRepositoryImpl implements PopularViewRankingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WeeklyViewIncreaseRow> findTopWeeklyViewIncreases(LocalDate weekStart, int limit) {
        QBill bill = QBill.bill;
        QBillViewWeekBaseline baseline = QBillViewWeekBaseline.billViewWeekBaseline;

        NumberExpression<Long> weeklyDelta = Expressions.numberTemplate(
                Long.class,
                "GREATEST({0} - coalesce({1}, 0), 0)",
                bill.viewCount,
                baseline.baselineViewCount);

        return queryFactory
                .select(Projections.constructor(
                        WeeklyViewIncreaseRow.class,
                        bill.id,
                        weeklyDelta))
                .from(bill)
                .leftJoin(baseline).on(
                        baseline.id.billId.eq(bill.id),
                        baseline.id.weekStart.eq(weekStart))
                .where(weeklyDelta.gt(0L))
                .orderBy(weeklyDelta.desc(), bill.viewCount.desc(), bill.id.asc())
                .limit(limit)
                .fetch();
    }
}
