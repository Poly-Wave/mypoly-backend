package com.polywave.notificationservice.api.controller.internal;

import com.polywave.notificationservice.api.spec.InternalUserNotificationApi;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * user-service 회원 탈퇴 시 호출하는 사용자 알림 삭제 internal API.
 * AdminApiKeyFilter 가 X-Admin-Api-Key 헤더로 가드한다.
 */
@RestController
@RequiredArgsConstructor
public class InternalUserNotificationController implements InternalUserNotificationApi {

    private final UserNotificationCommandService userNotificationCommandService;

    @Override
    public ResponseEntity<Void> deleteUserNotifications(Long userId) {
        userNotificationCommandService.deleteAllByUser(userId);
        return ResponseEntity.noContent().build();
    }
}
