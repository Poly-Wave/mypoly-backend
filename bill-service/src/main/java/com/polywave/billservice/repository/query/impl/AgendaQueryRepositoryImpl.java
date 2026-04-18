package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import com.polywave.billservice.domain.AgeBand;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgendaQueryRepositoryImpl implements AgendaQueryRepository {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");

    private static final String VOTE_RESULT_AGREE = UserVoteResult.AGREE.name();
    private static final String VOTE_RESULT_DISAGREE = UserVoteResult.DISAGREE.name();

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgendaResult> findHotDebateAgendas(Long userId, int days, int minVoteCount, Pageable pageable) {
        QUserBillVote vote = QUserBillVote.userBillVote;
        QUserBillVote userVote = new QUserBillVote("userVote");
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

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
                        totalVoteCount,
                        hasVoted,
                        category.code
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId)
                )
                .where(vote.votedAt.goe(cutoff))
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        category.code
                )
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(controversyScore.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<AgendaResult> findTrendingAgendas(Long userId, int days, Pageable pageable) {
        QBillTrendingSnapshot snapshot = QBillTrendingSnapshot.billTrendingSnapshot;
        QBill bill = QBill.bill;
        QUserBillVote vote = QUserBillVote.userBillVote;
        QUserBillVote voteSub = new QUserBillVote("voteSub");
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;
        Instant voteCutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        NumberExpression<Integer> agreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> agreeSum = agreeCase.sum();
        NumberExpression<Integer> disagreeSum = disagreeCase.sum();
        NumberExpression<Double> agreeRatio = Expressions.numberTemplate(
                Double.class,
                "coalesce((1.0 * {0}) / nullif(({0} + {1}), 0), 0.0)",
                agreeSum,
                disagreeSum
        );

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
                                agreeRatio,
                                snapshot.voteCount7d.longValue(),
                                hasVoted,
                                category.code
                        )
                )
                .from(snapshot)
                .innerJoin(bill).on(snapshot.billId.eq(bill.id))
                .leftJoin(vote).on(
                        vote.bill.id.eq(bill.id),
                        vote.votedAt.goe(voteCutoff)
                )
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .leftJoin(billAiCategory.category, category)
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        snapshot.voteCount7d,
                        category.code
                )
                .orderBy(snapshot.voteCount7d.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<AgendaResult> findRecent30dAgendas(Long userId, int minVoteCount, Pageable pageable) {
        QBill bill = QBill.bill;
        QUserBillVote vote = QUserBillVote.userBillVote;
        QUserBillVote userVote = new QUserBillVote("userVote");
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        Instant voteCutoff = Instant.now().minus(30, ChronoUnit.DAYS);

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
                "coalesce((1.0 * {0}) / nullif(({0} + {1}), 0), 0.0)",
                agreeSum,
                disagreeSum
        );

        NumberExpression<Integer> hasVotedInt = Expressions.cases()
                .when(userVote.id.isNotNull()).then(1)
                .otherwise(0)
                .max();

        BooleanExpression hasVoted = hasVotedInt.eq(1);

        return queryFactory
                .select(Projections.constructor(
                        AgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        agreeRatio,
                        totalVoteCount,
                        hasVoted,
                        category.code
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId)
                )
                .where(vote.votedAt.goe(voteCutoff))
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        category.code
                )
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(totalVoteCount.desc(), bill.proposalDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<AgendaResult> findSameAgeAgendas(
            Long userId,
            AgeBand ageBand,
            int days,
            int minVoteCount,
            Pageable pageable
    ) {
        QBill bill = QBill.bill;
        QUserBillVote vote = QUserBillVote.userBillVote;
        QUserBillVote userVote = new QUserBillVote("userVote");
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        Instant voteCutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        NumberExpression<Integer> agreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = Expressions.cases()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> agreeSum = agreeCase.sum();
        NumberExpression<Integer> disagreeSum = disagreeCase.sum();
        NumberExpression<Double> agreeRatio = Expressions.numberTemplate(
                Double.class,
                "coalesce((1.0 * {0}) / nullif(({0} + {1}), 0), 0.0)",
                agreeSum,
                disagreeSum
        );
        NumberExpression<Long> sameAgeVoteCount = vote.id.count();
        NumberExpression<Integer> hasVotedInt = Expressions.cases()
                .when(userVote.id.isNotNull()).then(1)
                .otherwise(0)
                .max();
        BooleanExpression hasVoted = hasVotedInt.eq(1);

        return queryFactory
                .select(Projections.constructor(
                        AgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        agreeRatio,
                        sameAgeVoteCount,
                        hasVoted,
                        category.code
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId)
                )
                .where(
                        vote.voterAgeBand.eq(ageBand.name()),
                        vote.votedAt.goe(voteCutoff)
                )
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        category.code
                )
                .having(sameAgeVoteCount.goe((long) minVoteCount))
                .orderBy(sameAgeVoteCount.desc(), bill.proposalDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<MainAgendaResult> findMainAgendas(
            Long userId,
            boolean applyInterestFilter,
            Set<Long> interestCategoryIds,
            Pageable pageable
    ) {
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;
        QUserBillVote vote = QUserBillVote.userBillVote;
        NumberExpression<Long> viewCount = bill.viewCount;

        NumberExpression<Long> voteCount = vote.id.count();

        BooleanExpression interestFilter = null;
        if (applyInterestFilter) {
            interestFilter = category.id.in(interestCategoryIds);
        }

        boolean isPopularSort = pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equalsIgnoreCase("popular"));

        OrderSpecifier<?> primaryOrder = isPopularSort
                ? viewCount.desc()
                : bill.proposalDate.desc();

        return queryFactory
                .select(Projections.constructor(
                        MainAgendaResult.class,
                        bill.officialTitle,
                        analysis.summary,
                        Expressions.nullExpression(String.class),
                        bill.proposalDate,
                        viewCount,
                        voteCount,
                        category.code,
                        category.name,
                        category.backgroundColor
                ))
                .from(bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .leftJoin(billAiCategory.category, category)
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .where(interestFilter)
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        analysis.summary,
                        bill.proposalDate,
                        category.code,
                        category.name,
                        category.id
                )
                .orderBy(primaryOrder, bill.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}