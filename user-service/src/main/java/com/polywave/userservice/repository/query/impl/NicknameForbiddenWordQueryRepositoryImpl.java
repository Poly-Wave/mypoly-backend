package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.domain.QNicknameForbiddenWord;
import com.polywave.userservice.repository.query.NicknameForbiddenWordQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NicknameForbiddenWordQueryRepositoryImpl implements NicknameForbiddenWordQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean containsForbiddenWord(String nickname) {
        Integer result = queryFactory
                .selectOne()
                .from(QNicknameForbiddenWord.nicknameForbiddenWord)
                .where(
                        QNicknameForbiddenWord.nicknameForbiddenWord.active.isTrue(),
                        nicknameContainsForbiddenWord(nickname)
                )
                .fetchFirst();

        return result != null;
    }

    private BooleanExpression nicknameContainsForbiddenWord(String nickname) {
        return Expressions.booleanTemplate(
                "{0} like concat('%', {1}, '%')",
                Expressions.constant(nickname),
                QNicknameForbiddenWord.nicknameForbiddenWord.word
        );
    }
}
