package com.polywave.notificationservice.repository.query;

import com.polywave.notificationservice.domain.notification.UserNotification;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserNotificationQueryRepository {

    /**
     * 사용자 알림함 목록 (soft-delete 제외, 최신순).
     * pageSize + 1 만큼 조회하여 다음 페이지 존재 여부 판단에 활용한다.
     */
    List<UserNotification> findMyNotifications(Long userId, Pageable pageable);

    long countUnread(Long userId);
}
