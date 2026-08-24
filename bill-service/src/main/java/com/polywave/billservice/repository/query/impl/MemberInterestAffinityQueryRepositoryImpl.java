package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.member.query.result.CategoryCountResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QBillProposer;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.MemberInterestAffinityQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberInterestAffinityQueryRepositoryImpl implements MemberInterestAffinityQueryRepository {

    /** 의안의 대표 카테고리(AI 분석 카테고리 중 rankOrder 1) 기준으로만 집계한다. */
    private static final int PRIMARY_CATEGORY_RANK_ORDER = 1;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryCountResult> findMemberProposalCountsByCategory(Long memberId) {
        QBillProposer proposer = QBillProposer.billProposer;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory primaryBillAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory primaryCategory = new QBillCategory("primaryCategory");

        return queryFactory
                .select(Projections.constructor(
                        CategoryCountResult.class,
                        primaryCategory.id,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        // bill_proposers 에 (bill_id, member_id) 유니크 제약이 없어 중복 행이 있을 수 있으므로
                        // 의안 단위로 중복 제거해서 센다.
                        bill.id.countDistinct()
                ))
                .from(proposer)
                .innerJoin(proposer.bill, bill)
                .innerJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .innerJoin(primaryBillAiCategory).on(
                        primaryBillAiCategory.analysis.id.eq(analysis.id),
                        primaryBillAiCategory.rankOrder.eq(PRIMARY_CATEGORY_RANK_ORDER)
                )
                .innerJoin(primaryBillAiCategory.category, primaryCategory)
                // 대표발의(is_representative = true)와 공동발의를 모두 포함한다.
                .where(proposer.member.id.eq(memberId))
                .groupBy(
                        primaryCategory.id,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor
                )
                .fetch();
    }

    @Override
    public List<CategoryCountResult> findUserVoteCountsByCategory(Long userId) {
        QUserBillVote vote = QUserBillVote.userBillVote;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory primaryBillAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory primaryCategory = new QBillCategory("primaryCategory");

        return queryFactory
                .select(Projections.constructor(
                        CategoryCountResult.class,
                        primaryCategory.id,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        // user_bill_votes 는 (user_id, bill_id) 유니크라 의안 중복이 없다.
                        vote.id.count()
                ))
                .from(vote)
                .innerJoin(analysis).on(
                        analysis.bill.id.eq(vote.bill.id),
                        analysis.current.isTrue()
                )
                .innerJoin(primaryBillAiCategory).on(
                        primaryBillAiCategory.analysis.id.eq(analysis.id),
                        primaryBillAiCategory.rankOrder.eq(PRIMARY_CATEGORY_RANK_ORDER)
                )
                .innerJoin(primaryBillAiCategory.category, primaryCategory)
                .where(vote.userId.eq(userId))
                .groupBy(
                        primaryCategory.id,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor
                )
                .fetch();
    }
}
