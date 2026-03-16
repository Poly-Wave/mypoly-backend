package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.result.HotDebateRow;
import com.polywave.billservice.domain.QAssemblyBill;
import com.polywave.billservice.domain.QAssemblyBillVote;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.agenda.HotDebateCriteria;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgendaQueryRepositoryImpl implements AgendaQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgendaResult> findHotDebateAgendas(Pageable pageable) {
        QAssemblyBillVote vote = QAssemblyBillVote.assemblyBillVote;
        QAssemblyBill bill = QAssemblyBill.assemblyBill;

        NumberExpression<Integer> agreeCase = Expressions.cases()
                .when(vote.voteResult.eq(HotDebateCriteria.VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = Expressions.cases()
                .when(vote.voteResult.eq(HotDebateCriteria.VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> agreeSum = agreeCase.sum();
        NumberExpression<Integer> disagreeSum = disagreeCase.sum();

        // |찬성% - 반대%| = |agree/(agree+disagree) - 0.5|, 오름차순 → 가장 작을수록 상위
        NumberExpression<Double> controversyScore = Expressions.numberTemplate(
                Double.class,
                "abs(cast({0} as double precision) / (cast({0} as double precision) + cast({1} as double precision)) - 0.5)",
                agreeSum,
                disagreeSum
        );

        List<HotDebateRow> rows = queryFactory
                .select(
                        Projections.constructor(
                                HotDebateRow.class,
                                bill.id,
                                bill.officialTitle,
                                bill.proposalDate,
                                bill.detailUrl,
                                agreeSum.longValue(),
                                disagreeSum.longValue()
                        )
                )
                .from(vote)
                .innerJoin(vote.bill, bill)
                .groupBy(bill.id, bill.officialTitle, bill.proposalDate, bill.detailUrl)
                .having(agreeSum.add(disagreeSum).goe((long) HotDebateCriteria.MIN_VOTE_COUNT))
                .orderBy(controversyScore.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return rows.stream().map(HotDebateRow::toAgendaResult).toList();
    }

    @Override
    public List<AgendaResult> findTrendingAgendas(Pageable pageable) {
        QBillTrendingSnapshot snapshot = QBillTrendingSnapshot.billTrendingSnapshot;
        QAssemblyBill bill = QAssemblyBill.assemblyBill;

        return queryFactory
                .select(
                        Projections.constructor(
                                AgendaResult.class,
                                bill.id,
                                bill.officialTitle,
                                bill.proposalDate,
                                bill.detailUrl,
                                Expressions.constant(0.0),
                                Expressions.constant(0.0),
                                snapshot.voteCount7d.longValue()
                        )
                )
                .from(snapshot)
                .innerJoin(bill).on(snapshot.billId.eq(bill.id))
                .orderBy(snapshot.voteCount7d.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
