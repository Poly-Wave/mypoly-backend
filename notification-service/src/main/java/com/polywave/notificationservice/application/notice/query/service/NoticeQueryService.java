package com.polywave.notificationservice.application.notice.query.service;

import com.polywave.notificationservice.application.notice.query.result.NoticeDetailResult;
import com.polywave.notificationservice.application.notice.query.result.NoticeSummaryResult;
import com.polywave.notificationservice.common.exception.NoticeNotFoundException;
import com.polywave.notificationservice.repository.query.NoticeQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeQueryRepository noticeQueryRepository;

    public NoticePage findNotices(Pageable pageable) {
        List<NoticeSummaryResult> rows = noticeQueryRepository.findVisibleNotices(pageable);

        int pageSize = pageable.getPageSize();
        boolean hasNext = rows.size() > pageSize;
        List<NoticeSummaryResult> content = rows.stream().limit(pageSize).toList();

        return new NoticePage(content, pageable.getPageNumber(), pageSize, hasNext);
    }

    public NoticeDetailResult getNotice(Long noticeId) {
        return noticeQueryRepository.findVisibleNoticeById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);
    }

    public record NoticePage(
            List<NoticeSummaryResult> content,
            int page,
            int size,
            boolean hasNext
    ) {
    }
}
