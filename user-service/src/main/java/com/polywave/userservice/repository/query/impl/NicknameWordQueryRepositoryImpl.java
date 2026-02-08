package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.domain.NicknameWordType;
import com.polywave.userservice.domain.QNicknameWord;
import com.polywave.userservice.repository.query.NicknameWordQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ThreadLocalRandom;

@Repository
@RequiredArgsConstructor
public class NicknameWordQueryRepositoryImpl implements NicknameWordQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public String pickRandom(NicknameWordType type) {
        QNicknameWord word = QNicknameWord.nicknameWord;

        Long count = queryFactory
                .select(word.count())
                .from(word)
                .where(
                        word.type.eq(type),
                        word.active.isTrue()
                )
                .fetchOne();

        if (count == null || count == 0) {
            throw new IllegalStateException("닉네임 단어가 없습니다: " + type);
        }

        // 랜덤 뽑기
        long offset = ThreadLocalRandom.current().nextLong(count);

        return queryFactory
                .select(word.word)
                .from(word)
                .where(
                        word.type.eq(type),
                        word.active.isTrue()
                )
                .offset(offset)
                .limit(1)
                .fetchOne();
    }
}
