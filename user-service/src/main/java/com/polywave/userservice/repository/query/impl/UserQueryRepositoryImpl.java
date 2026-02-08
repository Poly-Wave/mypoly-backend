package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.domain.QUser;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.query.UserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByNickname(String nickname) {
        Integer result = jpaQueryFactory
                .selectOne()
                .from(QUser.user)
                .where(QUser.user.nickname.eq(nickname))
                .fetchFirst();

        return result != null;
    }
}
