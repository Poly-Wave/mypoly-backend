package com.polywave.notificationservice.repository.query;

import com.polywave.notificationservice.application.notice.query.result.NoticeDetailResult;
import com.polywave.notificationservice.application.notice.query.result.NoticeSummaryResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface NoticeQueryRepository {

    List<NoticeSummaryResult> findVisibleNotices(Pageable pageable);

    Optional<NoticeDetailResult> findVisibleNoticeById(Long id);
}
