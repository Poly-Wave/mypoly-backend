package com.polywave.notificationservice.application.notification.user.query.service;

import com.polywave.notificationservice.application.notification.user.query.result.UserNotificationResult;
import com.polywave.notificationservice.domain.notification.UserNotification;
import com.polywave.notificationservice.repository.query.UserNotificationQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserNotificationQueryService {

    private final UserNotificationQueryRepository userNotificationQueryRepository;

    public MyNotificationPage findMyNotifications(Long userId, Pageable pageable) {
        List<UserNotification> rows = userNotificationQueryRepository.findMyNotifications(userId, pageable);

        int pageSize = pageable.getPageSize();
        boolean hasNext = rows.size() > pageSize;
        List<UserNotificationResult> content = rows.stream()
                .limit(pageSize)
                .map(UserNotificationResult::from)
                .toList();

        return new MyNotificationPage(content, pageable.getPageNumber(), pageSize, hasNext);
    }

    public long countUnread(Long userId) {
        return userNotificationQueryRepository.countUnread(userId);
    }

    public record MyNotificationPage(
            List<UserNotificationResult> content,
            int page,
            int size,
            boolean hasNext
    ) {
    }
}
