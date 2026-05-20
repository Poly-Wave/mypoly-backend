package com.polywave.notificationservice.application.notification.user.command.service;

import com.polywave.notificationservice.application.notification.push.PushSender;
import com.polywave.notificationservice.application.notification.template.MessageTemplateRenderer;
import com.polywave.notificationservice.common.exception.NotificationPolicyNotFoundException;
import com.polywave.notificationservice.common.exception.UserNotificationNotFoundException;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.domain.notification.UserNotification;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import com.polywave.notificationservice.repository.command.UserNotificationCommandRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 알림 이력 command.
 *
 * - 알림 생성(=발송)은 정책 ID 기반으로 호출하거나, 정책 없이도 발급할 수 있다.
 * - dedupKey 가 주어지면 (userId, dedupKey) UNIQUE 로 중복 발송을 방지한다.
 * - PUSH 채널일 때만 PushSender 호출. (No-op 구현체가 기본)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserNotificationCommandService {

    private final UserNotificationCommandRepository userNotificationCommandRepository;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final PushSender pushSender;
    private final MessageTemplateRenderer messageTemplateRenderer;

    /**
     * 정책 기반 알림 발급 (변수 치환 없음).
     * - DEV 트리거 (/dev-notifications/deliver) 가 유일한 호출처.
     * - 운영 스케줄러는 templateVars 가 있는 5-인자 overload 를 사용한다.
     */
    public Optional<UserNotification> deliverFromPolicy(
            Long userId,
            Long policyId,
            Long landingId,
            String dedupKey
    ) {
        return deliverFromPolicy(userId, policyId, landingId, dedupKey, Map.of());
    }

    /**
     * 정책 기반으로 사용자 알림을 생성한다.
     * - 정책 본문/타이틀/카테고리/랜딩 정보를 발급 시점에 스냅샷으로 복사하여 정책이 사후에 수정되어도
     *   알림함 표시가 깨지지 않게 한다.
     * - templateVars 가 주어지면 본문/타이틀의 {key} 자리를 치환한 결과를 스냅샷에 저장한다.
     * - 정책의 status 가 ACTIVE 가 아니면 발송하지 않는다. (스케줄러 재실행 안전성)
     *
     * @return 새로 발급되었으면 발급된 알림, 중복으로 skip 된 경우 Optional.empty()
     */
    public Optional<UserNotification> deliverFromPolicy(
            Long userId,
            Long policyId,
            Long landingId,
            String dedupKey,
            Map<String, String> templateVars
    ) {
        NotificationPolicy policy = notificationPolicyCommandRepository.findById(policyId)
                .orElseThrow(NotificationPolicyNotFoundException::new);

        if (policy.getStatus() != NotificationPolicyStatus.ACTIVE) {
            log.debug("Skip delivery: policy not active. policyId={}, status={}", policyId, policy.getStatus());
            return Optional.empty();
        }

        String renderedTitle = messageTemplateRenderer.render(policy.getTitle(), templateVars);
        String renderedBody = messageTemplateRenderer.render(policy.getBody(), templateVars);

        UserNotification draft = UserNotification.create(
                userId,
                policy.getId(),
                policy.getChannel(),
                policy.getCategory(),
                renderedTitle,
                renderedBody,
                policy.getLandingType(),
                landingId,
                policy.getLandingUrl(),
                dedupKey,
                Instant.now()
        );

        return persistWithDedup(draft);
    }

    public void markAsRead(Long userId, Long notificationId) {
        UserNotification notification = userNotificationCommandRepository
                .findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
                .orElseThrow(UserNotificationNotFoundException::new);

        notification.markAsRead(Instant.now());
    }

    public int markAllAsRead(Long userId) {
        return userNotificationCommandRepository.markAllAsRead(userId, Instant.now());
    }

    private Optional<UserNotification> persistWithDedup(UserNotification draft) {
        // 사전 dedup 체크.
        // - 같은 트랜잭션 안에서 unique 충돌을 INSERT 후 catch 하려고 하면 Spring 이
        //   이미 rollback-only 로 마크한 트랜잭션을 commit 하다 UnexpectedRollbackException 으로 500 이 된다.
        // - 따라서 INSERT 전에 SELECT 로 먼저 체크하여 99%의 멱등 케이스를 처리한다.
        // - 동시 race 발생 시(극히 드묾) 두 번째 트랜잭션은 unique violation 으로 롤백되며,
        //   클라이언트가 동일 요청을 재시도하면 사전 체크에서 잡혀 멱등 응답이 보장된다.
        String dedupKey = draft.getDedupKey();
        if (dedupKey != null && !dedupKey.isBlank()
                && userNotificationCommandRepository.existsByUserIdAndDedupKey(draft.getUserId(), dedupKey)) {
            log.debug(
                    "Duplicate user notification skipped (pre-check). userId={}, dedupKey={}",
                    draft.getUserId(),
                    dedupKey
            );
            return Optional.empty();
        }

        UserNotification saved = userNotificationCommandRepository.save(draft);

        if (saved.getChannel() == NotificationChannel.PUSH) {
            pushSender.send(saved);
        }

        return Optional.of(saved);
    }
}
