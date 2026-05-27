package com.polywave.notificationservice.repository.query.impl;

import com.polywave.notificationservice.domain.notification.QUserNotification;
import com.polywave.notificationservice.domain.notification.UserNotification;
import com.polywave.notificationservice.repository.query.UserNotificationQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserNotificationQueryRepositoryImpl implements UserNotificationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserNotification> findMyNotifications(Long userId, Pageable pageable) {
        QUserNotification n = QUserNotification.userNotification;

        return jpaQueryFactory
                .selectFrom(n)
                .where(
                        n.userId.eq(userId),
                        n.deletedAt.isNull()
                )
                .orderBy(n.createdAt.desc(), n.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
    }

    @Override
    public long countUnread(Long userId) {
        QUserNotification n = QUserNotification.userNotification;

        Long count = jpaQueryFactory
                .select(n.count())
                .from(n)
                .where(
                        n.userId.eq(userId),
                        n.readAt.isNull(),
                        n.deletedAt.isNull()
                )
                .fetchOne();

        return count == null ? 0L : count;
    }
}
