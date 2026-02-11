package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.QTerms;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TermsQueryRepositoryImpl implements TermsQueryRepository {
    private final JPAQueryFactory queryFactory;

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
                .orderBy(term.required.desc(), term.name.asc(), term.id.asc())
                .fetch();
    }

    @Override
    public Optional<TermsResult> findTermsMetaById(Long termsId) {
        QTerms term = QTerms.terms;

        TermsResult result = queryFactory
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
                .where(term.id.eq(termsId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TermsDetailResult> findTermsDetailById(Long termsId) {
        QTerms term = QTerms.terms;

        TermsDetailResult result = queryFactory
                .select(Projections.constructor(
                        TermsDetailResult.class,
                        term.id,
                        term.name,
                        term.title,
                        term.version,
                        term.required,
                        term.content,
                        term.effectiveFrom
                ))
                .from(term)
                .where(term.id.eq(termsId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
