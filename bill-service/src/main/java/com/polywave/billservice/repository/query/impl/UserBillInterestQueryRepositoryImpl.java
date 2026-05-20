package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.application.category.query.result.UserCategoryInterestResult;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QUserBillInterest;
import com.polywave.billservice.repository.query.UserBillInterestQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBillInterestQueryRepositoryImpl
        implements UserBillInterestQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserCategoryInterestResult> findCategoryIdsByUserId(Long userId) {
        QUserBillInterest userBillInterest = QUserBillInterest.userBillInterest;

        return queryFactory
                .select(Projections.constructor(
                        UserCategoryInterestResult.class,
                        userBillInterest.category.id
                ))
                .from(userBillInterest)
                .where(userBillInterest.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<CategoryResult> findActiveCategoriesByUserId(Long userId) {
        QUserBillInterest userBillInterest = QUserBillInterest.userBillInterest;
        QBillCategory category = QBillCategory.billCategory;

        return queryFactory
                .select(Projections.constructor(
                        CategoryResult.class,
                        category.code,
                        category.name,
                        category.displayOrder,
                        category.backgroundColor
                ))
                .from(userBillInterest)
                .join(userBillInterest.category, category)
                .where(
                        userBillInterest.userId.eq(userId),
                        category.isActive.isTrue()
                )
                .orderBy(category.displayOrder.asc())
                .fetch();
    }
}