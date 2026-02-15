package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.domain.AdministrativeArea;
import com.polywave.userservice.repository.query.AdministrativeAreaQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.polywave.userservice.domain.QAdministrativeArea.administrativeArea;

@Repository
@RequiredArgsConstructor
public class AdministrativeAreaQueryRepositoryImpl implements AdministrativeAreaQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdministrativeArea> searchByKeyword(String keyword, Pageable pageable) {
        BooleanExpression predicate = buildPredicate(keyword);

        List<AdministrativeArea> content = queryFactory
                .selectFrom(administrativeArea)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(administrativeArea.sido.asc(), administrativeArea.sigungu.asc(),
                        administrativeArea.emdName.asc())
                .fetch();

        Long total = queryFactory
                .select(administrativeArea.count())
                .from(administrativeArea)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression buildPredicate(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        // 시도, 시군구, 읍면동 중 하나라도 키워드를 포함하면 검색 (OR 조건)
        return administrativeArea.sido.contains(keyword)
                .or(administrativeArea.sigungu.contains(keyword))
                .or(administrativeArea.emdName.contains(keyword));
    }
}
