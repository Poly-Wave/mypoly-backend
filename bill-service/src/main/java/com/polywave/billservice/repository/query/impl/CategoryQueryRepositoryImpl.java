package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.application.category.query.result.UserCategoryInterestResult;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.repository.query.CategoryQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryResult> findAllActiveOrderByDisplayOrder() {
        QBillCategory category = QBillCategory.billCategory;

        return queryFactory.select(Projections.constructor(
                        CategoryResult.class,
                        category.id,
                        category.code,
                        category.name,
                        category.displayOrder
                ))
                .from(category)
                .where(category.isActive.isTrue())
                .orderBy(category.displayOrder.asc())
                .fetch();
    }
}
