package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.repository.query.CategoryQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
                        category.code,
                        category.name,
                        category.displayOrder,
                        category.backgroundColor,
                        category.textColor
                ))
                .from(category)
                .where(category.isActive.isTrue())
                .orderBy(category.displayOrder.asc())
                .fetch();
    }

    @Override
    public Set<Long> findActiveCategoryIdsByCodes(Set<String> categoryCodes) {
        if (categoryCodes.isEmpty()) {
            return Set.of();
        }

        QBillCategory category = QBillCategory.billCategory;
        return queryFactory.select(category.id)
                .from(category)
                .where(
                        category.isActive.isTrue(),
                        category.code.in(categoryCodes)
                )
                .fetch()
                .stream()
                .collect(Collectors.toSet());
    }
}