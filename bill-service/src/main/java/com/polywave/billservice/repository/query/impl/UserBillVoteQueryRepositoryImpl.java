package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.vote.query.result.MyVotedBillResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.UserBillVoteQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBillVoteQueryRepositoryImpl implements UserBillVoteQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Long> findVoteIdByUserIdAndBillId(Long userId, Long billId) {
        QUserBillVote userBillVote = QUserBillVote.userBillVote;
        Long voteId = queryFactory
                .select(userBillVote.id)
                .from(userBillVote)
                .where(userBillVote.userId.eq(userId).and(userBillVote.bill.id.eq(billId)))
                .fetchOne();
        return Optional.ofNullable(voteId);
    }

    @Override
    public List<MyVotedBillResult> findMyVotedBills(
            Long userId,
            Instant fromVotedAt,
            Instant toVotedAtExclusive,
            Set<String> voteResults,
            Pageable pageable
    ) {
        QUserBillVote userBillVote = QUserBillVote.userBillVote;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory primaryBillAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory primaryCategory = new QBillCategory("primaryCategory");
        QUserBillVote voteCountSource = new QUserBillVote("voteCountSource");

        int pageSize = pageable.getPageSize();

        return queryFactory
                .select(Projections.constructor(
                        MyVotedBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        userBillVote.votedAt,
                        userBillVote.voteResult,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount.coalesce(0L),
                        voteCountSource.id.count()
                ))
                .from(userBillVote)
                .innerJoin(userBillVote.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(primaryBillAiCategory).on(
                        primaryBillAiCategory.analysis.id.eq(analysis.id),
                        primaryBillAiCategory.rankOrder.eq(1)
                )
                .leftJoin(primaryBillAiCategory.category, primaryCategory)
                .leftJoin(voteCountSource).on(voteCountSource.bill.id.eq(bill.id))
                .where(
                        userBillVote.userId.eq(userId),
                        votedAtGoe(userBillVote, fromVotedAt),
                        votedAtLt(userBillVote, toVotedAtExclusive),
                        voteResultIn(userBillVote, voteResults)
                )
                .groupBy(
                        userBillVote.id,
                        userBillVote.votedAt,
                        userBillVote.voteResult,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount
                )
                .orderBy(userBillVote.votedAt.desc(), bill.id.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    private BooleanExpression votedAtGoe(QUserBillVote userBillVote, Instant fromVotedAt) {
        if (fromVotedAt == null) {
            return null;
        }
        return userBillVote.votedAt.goe(fromVotedAt);
    }

    private BooleanExpression votedAtLt(QUserBillVote userBillVote, Instant toVotedAtExclusive) {
        if (toVotedAtExclusive == null) {
            return null;
        }
        return userBillVote.votedAt.lt(toVotedAtExclusive);
    }

    private BooleanExpression voteResultIn(QUserBillVote userBillVote, Set<String> voteResults) {
        if (voteResults == null || voteResults.isEmpty()) {
            return null;
        }
        return userBillVote.voteResult.in(voteResults);
    }
}