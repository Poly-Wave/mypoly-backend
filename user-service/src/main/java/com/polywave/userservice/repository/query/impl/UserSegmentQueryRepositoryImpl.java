package com.polywave.userservice.repository.query.impl;

import com.polywave.userservice.application.user.query.result.OnboardingReminderUserResult;
import com.polywave.userservice.domain.QUser;
import com.polywave.userservice.repository.query.UserSegmentQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserSegmentQueryRepositoryImpl implements UserSegmentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OnboardingReminderUserResult> findUsersStuckAtNicknameBefore(Instant cutoff) {
        QUser u = QUser.user;
        List<Tuple> rows = jpaQueryFactory
                .select(u.id, u.nickname)
                .from(u)
                .where(
                        u.nicknameSetAt.isNotNull(),
                        u.categorySetAt.isNull(),
                        u.nicknameSetAt.lt(cutoff)
                )
                .fetch();

        return rows.stream()
                .map(t -> new OnboardingReminderUserResult(t.get(u.id), nullToEmpty(t.get(u.nickname))))
                .toList();
    }

    @Override
    public List<OnboardingReminderUserResult> findOnboardingCompletedUsers() {
        QUser u = QUser.user;
        List<Tuple> rows = jpaQueryFactory
                .select(u.id, u.nickname)
                .from(u)
                .where(u.profileCompletedAt.isNotNull())
                .fetch();

        return rows.stream()
                .map(t -> new OnboardingReminderUserResult(t.get(u.id), nullToEmpty(t.get(u.nickname))))
                .toList();
    }

    @Override
    public List<OnboardingReminderUserResult> findUsersStuckAtCategoryBefore(Instant cutoff) {
        QUser u = QUser.user;
        List<Tuple> rows = jpaQueryFactory
                .select(u.id, u.nickname)
                .from(u)
                .where(
                        u.categorySetAt.isNotNull(),
                        u.profileCompletedAt.isNull(),
                        u.categorySetAt.lt(cutoff)
                )
                .fetch();

        return rows.stream()
                .map(t -> new OnboardingReminderUserResult(t.get(u.id), nullToEmpty(t.get(u.nickname))))
                .toList();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
