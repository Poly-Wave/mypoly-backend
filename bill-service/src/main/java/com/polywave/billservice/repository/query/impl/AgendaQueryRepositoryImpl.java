package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.api.dto.MainAgendaSortType;
import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import com.polywave.billservice.application.agenda.query.result.PopularAgendaResult;
import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import com.polywave.billservice.domain.AgeBand;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QBillPopularViewRanking;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.QUserBillVote;
import java.time.LocalDate;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
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
        int pageSize = pageable.getPageSize();
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
                disagreeSum);

        NumberExpression<Integer> hasVotedInt = Expressions.cases()
                .when(userVote.id.isNotNull()).then(1)
                .otherwise(0)
                .max();

        BooleanExpression hasVoted = hasVotedInt.eq(1);

        NumberExpression<Double> controversyScore = Expressions.numberTemplate(
                Double.class,
                "abs(((1.0 * {0}) / ({0} + {1})) - 0.5)",
                agreeSum,
                disagreeSum);

        return queryFactory
                .select(Projections.constructor(
                        AgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        agreeRatio,
                        totalVoteCount,
                        hasVoted,
                        category.code))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId))
                .where(vote.votedAt.goe(cutoff))
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        category.code)
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(controversyScore.asc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<AgendaResult> findTrendingAgendas(Long userId, int days, Pageable pageable) {
        int pageSize = pageable.getPageSize();
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
                disagreeSum);

        var hasVoted = JPAExpressions
                .selectOne()
                .from(voteSub)
                .where(
                        voteSub.userId.eq(userId),
                        voteSub.bill.id.eq(bill.id))
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
                                category.code))
                .from(snapshot)
                .innerJoin(bill).on(snapshot.billId.eq(bill.id))
                .leftJoin(vote).on(
                        vote.bill.id.eq(bill.id),
                        vote.votedAt.goe(voteCutoff))
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        snapshot.voteCount7d,
                        category.code)
                .orderBy(snapshot.voteCount7d.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<AgendaResult> findRecent30dAgendas(Long userId, int minVoteCount, Pageable pageable) {
        int pageSize = pageable.getPageSize();
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
                disagreeSum);

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
                        category.code))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId))
                .where(vote.votedAt.goe(voteCutoff))
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        category.code)
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(totalVoteCount.desc(), bill.proposalDate.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<AgendaResult> findSameAgeAgendas(
            Long userId,
            AgeBand ageBand,
            int days,
            int minVoteCount,
            Pageable pageable) {
        int pageSize = pageable.getPageSize();
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
                disagreeSum);
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
                        category.code))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .leftJoin(userVote).on(
                        userVote.bill.id.eq(bill.id),
                        userVote.userId.eq(userId))
                .where(
                        vote.voterAgeBand.eq(ageBand.name()),
                        vote.votedAt.goe(voteCutoff))
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        category.code)
                .having(sameAgeVoteCount.goe((long) minVoteCount))
                .orderBy(sameAgeVoteCount.desc(), bill.proposalDate.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<MainAgendaResult> findMainAgendas(
            Long userId,
            boolean applyInterestFilter,
            Set<Long> interestCategoryIds,
            Set<String> categoryCodes,
            MainAgendaSortType sortType,
            Pageable pageable) {
        int pageSize = pageable.getPageSize();
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;
        QUserBillVote vote = QUserBillVote.userBillVote;
        QBillTrendingSnapshot snapshot = QBillTrendingSnapshot.billTrendingSnapshot;
        NumberExpression<Long> viewCount = bill.viewCount;
        NumberExpression<Long> voteCount = vote.id.count();
        NumberExpression<Long> voteCount7d = Expressions.numberTemplate(
                Long.class,
                "coalesce({0}, 0)",
                snapshot.voteCount7d);

        BooleanExpression interestFilter = null;
        if (applyInterestFilter) {
            interestFilter = category.id.in(interestCategoryIds);
        }
        BooleanExpression categoryCodeFilter = null;
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            categoryCodeFilter = category.code.in(categoryCodes);
        }

        boolean isPopularSort = sortType == MainAgendaSortType.POPULAR;

        NumberExpression<Long> selectedVoteCount = isPopularSort ? voteCount7d : voteCount;
        OrderSpecifier<?> primaryOrder = isPopularSort
                ? voteCount7d.desc()
                : bill.proposalDate.desc();

        return queryFactory
                .select(Projections.constructor(
                        MainAgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        Expressions.nullExpression(String.class),
                        bill.proposalDate,
                        viewCount,
                        selectedVoteCount,
                        category.code,
                        category.name,
                        category.backgroundColor,
                        category.textColor))
                .from(bill)
                .leftJoin(snapshot).on(snapshot.billId.eq(bill.id))
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .where(categoryCodeFilter, interestFilter)
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.proposalDate,
                        snapshot.voteCount7d,
                        category.code,
                        category.name,
                        category.id)
                .orderBy(primaryOrder, bill.id.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<SearchAgendaResult> searchAgendasByTitle(String keyword, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;
        QUserBillVote vote = QUserBillVote.userBillVote;

        NumberExpression<Long> viewCount = bill.viewCount;
        NumberExpression<Long> voteCount = vote.id.count();

        BooleanExpression keywordFilter = bill.officialTitle.containsIgnoreCase(keyword);

        return queryFactory
                .select(Projections.constructor(
                        SearchAgendaResult.class,
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        bill.proposalDate,
                        viewCount,
                        voteCount,
                        category.code,
                        category.name,
                        category.textColor))
                .from(bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .where(keywordFilter)
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        bill.proposalDate,
                        category.code,
                        category.name,
                        category.id)
                .orderBy(bill.proposalDate.desc(), bill.id.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    @Override
    public List<PopularAgendaResult> findPopularAgendas(Long userId, LocalDate weekStart) {
        QBillPopularViewRanking ranking = QBillPopularViewRanking.billPopularViewRanking;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;
        QUserBillVote vote = QUserBillVote.userBillVote;
        QUserBillVote voteSub = new QUserBillVote("voteSub");

        var hasVoted = JPAExpressions
                .selectOne()
                .from(voteSub)
                .where(
                        voteSub.userId.eq(userId),
                        voteSub.bill.id.eq(bill.id))
                .exists();

        NumberExpression<Long> viewCount = bill.viewCount.coalesce(0L);
        NumberExpression<Long> voteCount = vote.id.count();

        return queryFactory
                .select(Projections.constructor(
                        PopularAgendaResult.class,
                        ranking.id.rank.intValue(),
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        category.code,
                        category.name,
                        category.textColor,
                        bill.proposalDate,
                        viewCount,
                        ranking.viewCountWeekly,
                        ranking.previousRank,
                        voteCount,
                        hasVoted))
                .from(ranking)
                .innerJoin(bill).on(ranking.billId.eq(bill.id))
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue())
                .leftJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1))
                .leftJoin(billAiCategory.category, category)
                .where(ranking.id.weekStart.eq(weekStart))
                .groupBy(
                        ranking.id.rank,
                        ranking.viewCountWeekly,
                        ranking.previousRank,
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        bill.proposalDate,
                        category.code,
                        category.name,
                        category.textColor)
                .orderBy(ranking.id.rank.asc())
                .fetch();
    }
}