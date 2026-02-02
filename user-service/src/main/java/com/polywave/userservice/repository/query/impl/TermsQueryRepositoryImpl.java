package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.QTerms;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TermsQueryRepositoryImpl implements TermsQueryRepository {
    private final JPAQueryFactory queryFactory;

    // 서브 쿼리 사용해서, version이 가장 최신인 약관 목록 조회
    @Override
    public List<TermsResult> findLatestTerms() {
        QTerms term = QTerms.terms;
        QTerms sub = new QTerms("sub");

        return queryFactory
                .select(Projections.constructor(
                        TermsResult.class,
                        term.id,
                        term.name,
                        term.required
                ))
                .from(term)
                .where(
                        term.version.eq(
                                JPAExpressions
                                        .select(sub.version.max())
                                        .from(sub)
                        )
                )
                .fetch();
    }

}
