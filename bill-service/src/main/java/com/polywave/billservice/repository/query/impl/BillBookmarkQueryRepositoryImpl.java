package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.api.dto.BillBookmarkSortType;
import com.polywave.billservice.application.bill.query.service.BillUiStage;
import com.polywave.billservice.application.bookmark.query.result.BookmarkedBillResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QUserBillBookmark;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.BillBookmarkQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BillBookmarkQueryRepositoryImpl implements BillBookmarkQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsBookmark(Long userId, Long billId) {
        QUserBillBookmark bookmark = QUserBillBookmark.userBillBookmark;

        Integer result = queryFactory
                .selectOne()
                .from(bookmark)
                .where(
                        bookmark.userId.eq(userId),
                        bookmark.bill.id.eq(billId)
                )
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<BookmarkedBillResult> findBookmarkedBills(
            Long userId,
            Instant fromBookmarkedAt,
            Instant toBookmarkedAtExclusive,
            Set<String> categoryCodes,
            Set<String> stageCodes,
            BillBookmarkSortType sortType,
            Pageable pageable
    ) {
        QUserBillBookmark bookmark = QUserBillBookmark.userBillBookmark;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory primaryBillAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory primaryCategory = new QBillCategory("primaryCategory");
        QUserBillVote vote = QUserBillVote.userBillVote;

        int pageSize = pageable.getPageSize();

        return queryFactory
                .select(Projections.constructor(
                        BookmarkedBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        bill.proposalDate,
                        bookmark.createdAt,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount.coalesce(0L),
                        vote.id.count()
                ))
                .from(bookmark)
                .innerJoin(bookmark.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(primaryBillAiCategory).on(
                        primaryBillAiCategory.analysis.id.eq(analysis.id),
                        primaryBillAiCategory.rankOrder.eq(1)
                )
                .leftJoin(primaryBillAiCategory.category, primaryCategory)
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .where(
                        bookmark.userId.eq(userId),
                        bookmarkedAtGoe(bookmark, fromBookmarkedAt),
                        bookmarkedAtLt(bookmark, toBookmarkedAtExclusive),
                        categoryCodeIn(bill, categoryCodes),
                        stageCodeIn(bill, stageCodes)
                )
                .groupBy(
                        bookmark.id,
                        bookmark.createdAt,
                        bill.id,
                        bill.officialTitle,
                        analysis.headline,
                        bill.proposalDate,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount
                )
                .orderBy(bookmarkOrders(bookmark, bill, vote, sortType))
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();
    }

    private BooleanExpression bookmarkedAtGoe(QUserBillBookmark bookmark, Instant fromBookmarkedAt) {
        if (fromBookmarkedAt == null) {
            return null;
        }
        return bookmark.createdAt.goe(fromBookmarkedAt);
    }

    private BooleanExpression bookmarkedAtLt(QUserBillBookmark bookmark, Instant toBookmarkedAtExclusive) {
        if (toBookmarkedAtExclusive == null) {
            return null;
        }
        return bookmark.createdAt.lt(toBookmarkedAtExclusive);
    }

    private BooleanExpression categoryCodeIn(QBill bill, Set<String> categoryCodes) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return null;
        }

        QBillAiAnalysis filterAnalysis = new QBillAiAnalysis("filterAnalysis");
        QBillAiCategory filterBillAiCategory = new QBillAiCategory("filterBillAiCategory");
        QBillCategory filterCategory = new QBillCategory("filterCategory");

        return JPAExpressions
                .selectOne()
                .from(filterBillAiCategory)
                .innerJoin(filterBillAiCategory.analysis, filterAnalysis)
                .innerJoin(filterBillAiCategory.category, filterCategory)
                .where(
                        filterAnalysis.bill.id.eq(bill.id),
                        filterAnalysis.current.isTrue(),
                        filterCategory.code.in(categoryCodes)
                )
                .exists();
    }

    private Predicate stageCodeIn(QBill bill, Set<String> stageCodes) {
        if (stageCodes == null || stageCodes.isEmpty()) {
            return null;
        }

        BooleanBuilder builder = new BooleanBuilder();

        if (stageCodes.contains(BillUiStage.RECEIVED.code())) {
            builder.or(bill.currentProcStageOrder.isNull().or(bill.currentProcStageOrder.loe(1)));
        }
        if (stageCodes.contains(BillUiStage.REVIEW.code())) {
            builder.or(bill.currentProcStageOrder.eq(2));
        }
        if (stageCodes.contains(BillUiStage.DECISION.code())) {
            builder.or(bill.currentProcStageOrder.eq(3));
        }
        if (stageCodes.contains(BillUiStage.COMPLETED.code())) {
            builder.or(bill.currentProcStageOrder.goe(4));
        }

        if (!builder.hasValue()) {
            return null;
        }

        return builder;
    }

    private OrderSpecifier<?>[] bookmarkOrders(
            QUserBillBookmark bookmark,
            QBill bill,
            QUserBillVote vote,
            BillBookmarkSortType sortType
    ) {
        if (sortType == BillBookmarkSortType.POPULAR) {
            return new OrderSpecifier<?>[]{
                    vote.id.count().desc(),
                    bookmark.createdAt.desc(),
                    bill.id.desc()
            };
        }

        return new OrderSpecifier<?>[]{
                bookmark.createdAt.desc(),
                bill.id.desc()
        };
    }
}