package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.QTerms;
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

    /**
     * 약관 "종류(name)별" 최신 버전만 조회
     * - 목록 API에서는 본문(content)을 제외해 payload를 줄임
     */
    @Override
    public List<TermsResult> findLatestTerms() {
        QTerms term = QTerms.terms;
        QTerms sub = new QTerms("sub");

        return queryFactory
                .select(Projections.constructor(
                        TermsResult.class,
                        term.id,
                        term.name,
                        term.title,
                        term.version,
                        term.required,
                        term.effectiveFrom
                ))
                .from(term)
                .where(
                        term.version.eq(
                                JPAExpressions
                                        .select(sub.version.max())
                                        .from(sub)
                                        .where(sub.name.eq(term.name))
                        )
                )
                .orderBy(term.name.asc())
                .fetch();
    }
}
