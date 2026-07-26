package com.polywave.notificationservice.repository.query.impl;

import com.polywave.notificationservice.application.notice.query.result.NoticeDetailResult;
import com.polywave.notificationservice.application.notice.query.result.NoticeSummaryResult;
import com.polywave.notificationservice.domain.notice.QNotice;
import com.polywave.notificationservice.repository.query.NoticeQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<NoticeSummaryResult> findVisibleNotices(Pageable pageable) {
        QNotice notice = QNotice.notice;

        return jpaQueryFactory
                .select(Projections.constructor(
                        NoticeSummaryResult.class,
                        notice.id,
                        notice.title,
                        notice.createdAt
                ))
                .from(notice)
                .where(notice.visible.isTrue())
                .orderBy(notice.createdAt.desc(), notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
    }

    @Override
    public Optional<NoticeDetailResult> findVisibleNoticeById(Long id) {
        QNotice notice = QNotice.notice;

        NoticeDetailResult result = jpaQueryFactory
                .select(Projections.constructor(
                        NoticeDetailResult.class,
                        notice.id,
                        notice.title,
                        notice.content,
                        notice.createdAt
                ))
                .from(notice)
                .where(
                        notice.id.eq(id),
                        notice.visible.isTrue()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
