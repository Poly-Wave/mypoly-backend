package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
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
        QUserBillVote userVote = new QUserBillVote("userVote");
        QBill bill = QBill.bill;

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        NumberExpression<Integer> agreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> disagreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> agreeSum = agreeCase.sum();
        NumberExpression<Integer> disagreeSum = disagreeCase.sum();
        NumberExpression<Long> totalVoteCount = agreeSum.add(disagreeSum).longValue();

        NumberExpression<Double> agreeRatio = Expressions.numberTemplate(
                Double.class,
                "(1.0 * {0}) / ({0} + {1})",
                agreeSum,
                disagreeSum
        );

        NumberExpression<Double> disagreeRatio = Expressions.numberTemplate(
                Double.class,
                "(1.0 * {1}) / ({0} + {1})",
                agreeSum,
                disagreeSum
        );

        NumberExpression<Integer> hasVotedInt = Expressions.cases()
                .when(userVote.id.isNotNull()).then(1)
                .otherwise(0)
                .max();

        BooleanExpression hasVoted = hasVotedInt.eq(1);

        NumberExpression<Double> controversyScore = Expressions.numberTemplate(
                Double.class,
                "abs(((1.0 * {0}) / ({0} + {1})) - 0.5)",
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
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId)
                )
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
        QBill bill = QBill.bill;
        QUserBillVote voteSub = new QUserBillVote("voteSub");

        var hasVoted = JPAExpressions
                .selectOne()
                .from(voteSub)
                .where(
                        voteSub.userId.eq(userId),
                        voteSub.bill.id.eq(bill.id)
                )
                .exists();

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