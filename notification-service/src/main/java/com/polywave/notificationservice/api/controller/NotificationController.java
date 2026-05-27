package com.polywave.notificationservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.notificationservice.api.dto.MyNotificationListResponse;
import com.polywave.notificationservice.api.dto.UnreadNotificationCountResponse;
import com.polywave.notificationservice.api.spec.NotificationApi;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.application.notification.user.query.service.UserNotificationQueryService;
import com.polywave.notificationservice.application.notification.user.query.service.UserNotificationQueryService.MyNotificationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final UserNotificationQueryService userNotificationQueryService;
    private final UserNotificationCommandService userNotificationCommandService;

    @Override
    public ResponseEntity<MyNotificationListResponse> getMyNotifications(
            @LoginUser Long userId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MyNotificationPage result = userNotificationQueryService.findMyNotifications(userId, pageable);
        return ResponseEntity.ok(MyNotificationListResponse.from(result));
    }

    @Override
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(@LoginUser Long userId) {
        long count = userNotificationQueryService.countUnread(userId);
        return ResponseEntity.ok(UnreadNotificationCountResponse.of(count));
    }

    @Override
    public ResponseEntity<Void> markAsRead(Long notificationId, @LoginUser Long userId) {
        userNotificationCommandService.markAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> markAllAsRead(@LoginUser Long userId) {
        userNotificationCommandService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
