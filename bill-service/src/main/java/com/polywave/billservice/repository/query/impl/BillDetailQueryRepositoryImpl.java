package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.result.BillStatusHistoryResult;
import com.polywave.billservice.application.bill.query.result.BillVoteDetailResult;
import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import com.polywave.billservice.application.bill.query.result.SimilarTopicBillResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QBillStatusHistory;
import com.polywave.billservice.domain.QBillTrendingSnapshot;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.billservice.repository.query.BillDetailQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BillDetailQueryRepositoryImpl implements BillDetailQueryRepository {

    private static final String VOTE_RESULT_AGREE = UserVoteResult.AGREE.name();
    private static final String VOTE_RESULT_DISAGREE = UserVoteResult.DISAGREE.name();

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BillDetailResult> findBillDetailById(Long billId) {
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;

        BillDetailResult result = queryFactory
                .select(Projections.constructor(
                        BillDetailResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        bill.representativeProposerName,
                        bill.proposerCount,
                        bill.detailUrl,
                        bill.viewCount,
                        bill.currentProcStageCode,
                        bill.currentProcStageName,
                        bill.currentProcStageOrder,
                        bill.currentPassGubn,
                        bill.currentGeneralResult,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3
                ))
                .from(bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .where(bill.id.eq(billId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<BillCategoryResult> findCategoriesByBillId(Long billId) {
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        return queryFactory
                .select(Projections.constructor(
                        BillCategoryResult.class,
                        category.id,
                        category.code,
                        category.name,
                        category.textColor,
                        billAiCategory.rankOrder
                ))
                .from(billAiCategory)
                .innerJoin(billAiCategory.analysis, analysis)
                .innerJoin(billAiCategory.category, category)
                .where(
                        analysis.bill.id.eq(billId),
                        analysis.current.isTrue()
                )
                .orderBy(billAiCategory.rankOrder.asc(), category.displayOrder.asc())
                .fetch();
    }

    @Override
    public BillVoteSummaryResult findVoteSummaryByBillId(Long billId, Long userId) {
        QUserBillVote vote = QUserBillVote.userBillVote;

        NumberExpression<Integer> agreeCase = new CaseBuilder()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = new CaseBuilder()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> agreeSum = agreeCase.sum().coalesce(0);
        NumberExpression<Integer> disagreeSum = disagreeCase.sum().coalesce(0);

        Tuple tuple = queryFactory
                .select(agreeSum, disagreeSum)
                .from(vote)
                .where(vote.bill.id.eq(billId))
                .fetchOne();

        int agreeCount = tuple != null ? tuple.get(agreeSum) : 0;
        int disagreeCount = tuple != null ? tuple.get(disagreeSum) : 0;

        String myVoteResult = null;
        if (userId != null) {
            myVoteResult = queryFactory
                    .select(vote.voteResult)
                    .from(vote)
                    .where(
                            vote.bill.id.eq(billId),
                            vote.userId.eq(userId)
                    )
                    .fetchOne();
        }

        return BillVoteSummaryResult.of(myVoteResult, agreeCount, disagreeCount);
    }

    @Override
    public BillVoteDetailResult findVoteDetailByBillId(Long billId) {
        QUserBillVote vote = QUserBillVote.userBillVote;

        NumberExpression<Integer> agreeCase = new CaseBuilder()
                .when(vote.voteResult.eq(VOTE_RESULT_AGREE)).then(1)
                .otherwise(0);
        NumberExpression<Integer> disagreeCase = new CaseBuilder()
                .when(vote.voteResult.eq(VOTE_RESULT_DISAGREE)).then(1)
                .otherwise(0);

        NumberExpression<Integer> agreeSum = agreeCase.sum().coalesce(0);
        NumberExpression<Integer> disagreeSum = disagreeCase.sum().coalesce(0);

        Tuple overallTuple = queryFactory
                .select(agreeSum, disagreeSum)
                .from(vote)
                .where(vote.bill.id.eq(billId))
                .fetchOne();

        int agreeCount = overallTuple != null ? overallTuple.get(agreeSum) : 0;
        int disagreeCount = overallTuple != null ? overallTuple.get(disagreeSum) : 0;

        List<Tuple> ageBandTuples = queryFactory
                .select(vote.voterAgeBand, vote.count())
                .from(vote)
                .where(
                        vote.bill.id.eq(billId),
                        vote.voterAgeBand.isNotNull()
                )
                .groupBy(vote.voterAgeBand)
                .fetch();

        List<Tuple> genderTuples = queryFactory
                .select(vote.voterGender, vote.count())
                .from(vote)
                .where(
                        vote.bill.id.eq(billId),
                        vote.voterGender.isNotNull()
                )
                .groupBy(vote.voterGender)
                .fetch();

        return BillVoteDetailResult.of(
                agreeCount,
                disagreeCount,
                toCountMap(ageBandTuples, tuple -> tuple.get(vote.voterAgeBand)),
                toCountMap(genderTuples, tuple -> tuple.get(vote.voterGender))
        );
    }

    private Map<String, Long> toCountMap(List<Tuple> tuples, java.util.function.Function<Tuple, String> segmentExtractor) {
        Map<String, Long> counts = new HashMap<>();
        for (Tuple tuple : tuples) {
            String segment = segmentExtractor.apply(tuple);
            Long count = tuple.get(1, Long.class);
            if (segment == null || count == null) {
                continue;
            }
            counts.merge(segment, count, Long::sum);
        }
        return counts;
    }

    @Override
    public List<SimilarTopicBillResult> findSimilarTopicsByBillId(Long billId, int size) {
        Long primaryCategoryId = findPrimaryCategoryIdByBillId(billId);
        if (primaryCategoryId == null) {
            return List.of();
        }

        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        return queryFactory
                .select(Projections.constructor(
                        SimilarTopicBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                ))
                .from(billAiCategory)
                .innerJoin(billAiCategory.analysis, analysis)
                .innerJoin(analysis.bill, bill)
                .innerJoin(billAiCategory.category, category)
                .where(
                        analysis.current.isTrue(),
                        billAiCategory.rankOrder.eq(1),
                        category.id.eq(primaryCategoryId),
                        bill.id.ne(billId)
                )
                .orderBy(
                        bill.proposalDate.desc(),
                        bill.id.desc()
                )
                .limit(size)
                .fetch();
    }

    @Override
    public List<SimilarTopicBillResult> findHotDebateSimilarTopicsByBillId(
            Long billId,
            int days,
            int minVoteCount,
            int size
    ) {
        Long primaryCategoryId = findPrimaryCategoryIdByBillId(billId);
        if (primaryCategoryId == null) {
            return List.of();
        }

        QUserBillVote vote = QUserBillVote.userBillVote;
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

        NumberExpression<Double> controversyScore = Expressions.numberTemplate(
                Double.class,
                "abs(((1.0 * {0}) / ({0} + {1})) - 0.5)",
                agreeSum,
                disagreeSum
        );

        return queryFactory
                .select(Projections.constructor(
                        SimilarTopicBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .innerJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .innerJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .innerJoin(billAiCategory.category, category)
                .where(
                        vote.votedAt.goe(cutoff),
                        category.id.eq(primaryCategoryId),
                        bill.id.ne(billId)
                )
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                )
                .having(agreeSum.add(disagreeSum).goe((long) minVoteCount))
                .orderBy(
                        controversyScore.asc(),
                        totalVoteCount.desc(),
                        bill.proposalDate.desc(),
                        bill.id.desc()
                )
                .limit(size)
                .fetch();
    }

    @Override
    public List<SimilarTopicBillResult> findTrendingSimilarTopicsByBillId(Long billId, int size) {
        Long primaryCategoryId = findPrimaryCategoryIdByBillId(billId);
        if (primaryCategoryId == null) {
            return List.of();
        }

        QBillTrendingSnapshot snapshot = QBillTrendingSnapshot.billTrendingSnapshot;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        return queryFactory
                .select(Projections.constructor(
                        SimilarTopicBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                ))
                .from(snapshot)
                .innerJoin(bill).on(snapshot.billId.eq(bill.id))
                .innerJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .innerJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .innerJoin(billAiCategory.category, category)
                .where(
                        category.id.eq(primaryCategoryId),
                        bill.id.ne(billId)
                )
                .orderBy(
                        snapshot.voteCount7d.desc(),
                        bill.proposalDate.desc(),
                        bill.id.desc()
                )
                .limit(size)
                .fetch();
    }

    @Override
    public List<SimilarTopicBillResult> findMonthlyPopularSimilarTopicsByBillId(Long billId, int days, int size) {
        Long primaryCategoryId = findPrimaryCategoryIdByBillId(billId);
        if (primaryCategoryId == null) {
            return List.of();
        }

        QUserBillVote vote = QUserBillVote.userBillVote;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory billAiCategory = QBillAiCategory.billAiCategory;
        QBillCategory category = QBillCategory.billCategory;

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        return queryFactory
                .select(Projections.constructor(
                        SimilarTopicBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                ))
                .from(vote)
                .innerJoin(vote.bill, bill)
                .innerJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .innerJoin(billAiCategory).on(
                        billAiCategory.analysis.id.eq(analysis.id),
                        billAiCategory.rankOrder.eq(1)
                )
                .innerJoin(billAiCategory.category, category)
                .where(
                        vote.votedAt.goe(cutoff),
                        category.id.eq(primaryCategoryId),
                        bill.id.ne(billId)
                )
                .groupBy(
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        analysis.headline,
                        analysis.summary1,
                        analysis.summary2,
                        analysis.summary3,
                        bill.detailUrl,
                        category.id,
                        category.code,
                        category.name
                )
                .orderBy(
                        vote.id.count().desc(),
                        bill.proposalDate.desc(),
                        bill.id.desc()
                )
                .limit(size)
                .fetch();
    }

    @Override
    public List<BillStatusHistoryResult> findStatusHistoryByBillId(Long billId) {
        QBillStatusHistory history = QBillStatusHistory.billStatusHistory;

        return queryFactory
                .select(Projections.constructor(
                        BillStatusHistoryResult.class,
                        history.procDate,
                        history.procStageCode,
                        history.procStageName,
                        history.procStageOrder,
                        history.passGubn,
                        history.generalResult
                ))
                .from(history)
                .where(history.bill.id.eq(billId))
                .orderBy(
                        history.procDate.asc(),
                        history.observedAt.asc()
                )
                .fetch();
    }

    @Override
    public boolean existsBillById(Long billId) {
        QBill bill = QBill.bill;

        Integer result = queryFactory
                .selectOne()
                .from(bill)
                .where(bill.id.eq(billId))
                .fetchFirst();

        return result != null;
    }

    private Long findPrimaryCategoryIdByBillId(Long billId) {
        QBillAiAnalysis analysis = new QBillAiAnalysis("primaryAnalysis");
        QBillAiCategory billAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory category = new QBillCategory("primaryCategory");

        return queryFactory
                .select(category.id)
                .from(billAiCategory)
                .innerJoin(billAiCategory.analysis, analysis)
                .innerJoin(billAiCategory.category, category)
                .where(
                        analysis.bill.id.eq(billId),
                        analysis.current.isTrue(),
                        billAiCategory.rankOrder.eq(1)
                )
                .fetchOne();
    }
}