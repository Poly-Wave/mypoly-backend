package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.QUser;
import com.polywave.userservice.domain.QUserOauth;
import com.polywave.userservice.repository.query.UserQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

    @Override
    public OnBoardingStatus findOnboardingStatusByUserId(Long userId) {
        return jpaQueryFactory
                .select(QUser.user.onboardingStatus)
                .from(QUser.user)
                .where(QUser.user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public UserMeResult findUserMeByUserId(Long userId) {
        QUser u = QUser.user;
        QUserOauth o = QUserOauth.userOauth;

        Tuple t = jpaQueryFactory
                .select(
                        u.id,
                        o.provider,
                        o.providerUserId,
                        u.nickname,
                        u.onboardingStatus,
                        u.gender,
                        u.birthDate,
                        u.profileImageUrl,
                        u.sido,
                        u.sigungu,
                        u.emdName,
                        u.address
                )
                .from(u)
                .leftJoin(o).on(o.user.id.eq(u.id))
                .where(u.id.eq(userId))
                .fetchFirst();

        if (t == null) {
            return null;
        }

        return new UserMeResult(
                t.get(u.id),
                t.get(o.provider),
                t.get(o.providerUserId),
                t.get(u.nickname),
                t.get(u.onboardingStatus),
                t.get(u.gender),
                t.get(u.birthDate),
                t.get(u.profileImageUrl),
                t.get(u.sido),
                t.get(u.sigungu),
                t.get(u.emdName),
                t.get(u.address)
        );
    }
}