package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.domain.QAssemblyBill;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgendaQueryRepositoryImpl implements AgendaQueryRepository {

    private static final String VOTE_RESULT_AGREE = UserVoteResult.AGREE.name();
    private static final String VOTE_RESULT_DISAGREE = UserVoteResult.DISAGREE.name();

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgendaResult> findHotDebateAgendas(Long userId, int days, int minVoteCount, Pageable pageable) {
        QUserBillVote vote = QUserBillVote.userBillVote;
        QAssemblyBill bill = QAssemblyBill.assemblyBill;

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        NumberExpression<Integer> agreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> agreeSum = agreeCase.sum();
        NumberExpression<Integer> disagreeSum = disagreeCase.sum();
        NumberExpression<Integer> myVoteCase = Expressions.cases()
                .when(vote.userId.eq(userId)).then(1)
                .otherwise(0);
        NumberExpression<Integer> myVoteSum = myVoteCase.sum();
        NumberExpression<Long> totalVoteCount = agreeSum.add(disagreeSum).longValue();
        NumberExpression<Double> agreeRatio = Expressions.numberTemplate(
                Double.class,
                "cast({0} as double precision) / (cast({0} as double precision) + cast({1} as double precision))",
                agreeSum,
                disagreeSum
        );
        NumberExpression<Double> disagreeRatio = Expressions.numberTemplate(
                Double.class,
                "cast({1} as double precision) / (cast({0} as double precision) + cast({1} as double precision))",
                agreeSum,
                disagreeSum
        );
        var hasVoted = myVoteSum.gt(0);

        // |찬성% - 반대%| = |agree/(agree+disagree) - 0.5|, 오름차순 → 가장 작을수록 상위
        NumberExpression<Double> controversyScore = Expressions.numberTemplate(
                Double.class,
                "abs(cast({0} as double precision) / (cast({0} as double precision) + cast({1} as double precision)) - 0.5)",
                agreeSum,
                disagreeSum
        );

        return queryFactory
                .select(Projections.constructor(
                        AgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        agreeRatio,
                        disagreeRatio,
                        totalVoteCount,
                        hasVoted
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .where(vote.votedAt.goe(cutoff))
                .groupBy(bill.id, bill.officialTitle)
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(controversyScore.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<AgendaResult> findTrendingAgendas(Long userId, Pageable pageable) {
        QBillTrendingSnapshot snapshot = QBillTrendingSnapshot.billTrendingSnapshot;
        QAssemblyBill bill = QAssemblyBill.assemblyBill;
        var hasVoted = Expressions.booleanTemplate(
                "exists (select 1 from user_bill_votes ubv where ubv.user_id = {0} and ubv.bill_id = {1})",
                Expressions.constant(userId),
                bill.id
        );

        return queryFactory
                .select(
                        Projections.constructor(
                                AgendaResult.class,
                                bill.id,
                                bill.officialTitle,
                                Expressions.constant(0.0),
                                Expressions.constant(0.0),
                                snapshot.voteCount7d.longValue(),
                                hasVoted
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
