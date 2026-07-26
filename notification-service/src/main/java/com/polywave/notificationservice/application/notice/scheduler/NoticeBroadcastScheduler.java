package com.polywave.notificationservice.application.notice.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.UserServiceClient;
import com.polywave.notificationservice.client.dto.OnboardingReminderUserDto;
import com.polywave.notificationservice.domain.notice.Notice;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.repository.command.NoticeCommandRepository;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공지사항이 새로 등록(is_visible=true)되면 전체 유저에게 알림함(UserNotification) 항목을 자동 발급한다.
 *
 * 설계:
 * - 공지사항은 admin-tool 범용 CRUD로 직접 INSERT 되므로 애플리케이션 코드에 "생성 이벤트"가 없다.
 *   그래서 짧은 주기로 notices 테이블을 polling 하여 broadcast_at 이 비어 있는 건을 찾아 처리한다.
 * - 대상 유저: 관심 주제 선택 완료 유저 전원 (다른 홈 broadcast 스케줄러와 동일한 세그먼트).
 * - NOTICE_PUBLISHED_BROADCAST 정책이 시드는 되어 있지만 status=READY 인 동안은 실제 발송을 보류한다.
 *   (운영자가 ACTIVE 로 전환하기 전까지는 notices.broadcast_at 을 채우지 않고 계속 재시도 대상으로 남긴다.
 *    deliverFromPolicy 는 status!=ACTIVE 이면 항상 empty 를 반환하므로, 여기서 status 를 미리 확인해
 *    "실제로는 발송되지 않았는데 broadcast_at 만 채워지는" 상황을 방지한다.)
 *
 * 멱등성:
 * - dedupKey = "NOTICE_PUBLISHED_BROADCAST:{noticeId}:{userId}" — 재실행/재시도에도 중복 발급되지 않는다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NoticeBroadcastScheduler {

    private final UserServiceClient userServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final NoticeCommandRepository noticeCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /** 기본 5분 주기. */
    @Scheduled(fixedDelayString = "${notification.scheduler.notice-broadcast-fixed-delay-ms:300000}")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip notice broadcast: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        if (result.noticesBroadcasted() > 0) {
            log.info("NoticeBroadcastScheduler done. noticesBroadcasted={}, notificationsSent={}",
                    result.noticesBroadcasted(), result.notificationsSent());
        }
    }

    @Transactional
    public Result run() {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository
                .findByPolicyKey(SystemNotificationPolicyKey.NOTICE_PUBLISHED_BROADCAST);
        if (policyOpt.isEmpty() || policyOpt.get().getStatus() != NotificationPolicyStatus.ACTIVE) {
            log.debug("Skip notice broadcast: policy not seeded or not ACTIVE. policyKey={}",
                    SystemNotificationPolicyKey.NOTICE_PUBLISHED_BROADCAST);
            return new Result(0, 0);
        }
        Long policyId = policyOpt.get().getId();

        List<Notice> pending = noticeCommandRepository.findByVisibleTrueAndBroadcastAtIsNull();
        if (pending.isEmpty()) {
            return new Result(0, 0);
        }

        List<OnboardingReminderUserDto> targets = userServiceClient.findOnboardingCompletedUsers();

        int noticesBroadcasted = 0;
        int notificationsSent = 0;
        for (Notice notice : pending) {
            int sent = broadcastOne(notice, policyId, targets);
            notificationsSent += sent;
            notice.markBroadcasted(Instant.now());
            noticesBroadcasted++;
        }

        return new Result(noticesBroadcasted, notificationsSent);
    }

    private int broadcastOne(Notice notice, Long policyId, List<OnboardingReminderUserDto> targets) {
        int sent = 0;
        for (OnboardingReminderUserDto target : targets) {
            try {
                String dedupKey = SystemNotificationPolicyKey.NOTICE_PUBLISHED_BROADCAST
                        + ":" + notice.getId() + ":" + target.userId();
                Map<String, String> vars = Map.of("제목", notice.getTitle());
                Optional<?> result = userNotificationCommandService.deliverFromPolicy(
                        target.userId(), policyId, notice.getId(), dedupKey, vars
                );
                if (result.isPresent()) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("Notice broadcast delivery failed. noticeId={}, userId={}",
                        notice.getId(), target.userId(), e);
            }
        }
        return sent;
    }

    public record Result(int noticesBroadcasted, int notificationsSent) {
    }
}
