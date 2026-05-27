package com.polywave.notificationservice.repository.query.impl;

import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.domain.notification.QNotificationPolicy;
import com.polywave.notificationservice.repository.query.NotificationPolicyQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationPolicyQueryRepositoryImpl implements NotificationPolicyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<NotificationPolicy> findPolicies(
            NotificationPolicyStatus status,
            NotificationChannel channel,
            NotificationCategory category,
            Pageable pageable
    ) {
        QNotificationPolicy p = QNotificationPolicy.notificationPolicy;

        BooleanBuilder where = new BooleanBuilder();
        if (status != null) {
            where.and(p.status.eq(status));
        }
        if (channel != null) {
            where.and(p.channel.eq(channel));
        }
        if (category != null) {
            where.and(p.category.eq(category));
        }

        return jpaQueryFactory
                .selectFrom(p)
                .where(where)
                .orderBy(p.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
    }
}
