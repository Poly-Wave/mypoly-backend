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
 * - 같은 이유로 대상 유저가 0명이거나 모든 발송이 예외로 실패한 경우에도 broadcast_at 을 채우지 않는다.
 *   broadcast_at 이 한 번 채워지면 재시도 대상(broadcast_at IS NULL)에서 영구히 빠지기 때문이다.
 *
 * 멱등성:
 * - dedupKey = "NOTICE_PUBLISHED_BROADCAST:{noticeId}:{userId}" — 재실행/재시도에도 중복 발급되지 않는다.
 * - run() 전체를 하나의 트랜잭션으로 묶지 않는다. 한 유저의 dedup unique 충돌이 트랜잭션을
 *   rollback-only 로 만들어 그 실행에서 이미 발급된 모든 알림까지 되돌리는 것을 막기 위함이며,
 *   다른 broadcast 스케줄러들과 동일한 패턴이다(발급 1건 = 트랜잭션 1개).
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
        if (targets.isEmpty()) {
            // UserServiceClient 는 internal API 키 미설정이나 호출 실패 시에도 예외 없이 빈 목록을 돌려준다.
            // 여기서 막지 않으면 아무에게도 발송되지 않은 공지에 broadcast_at 이 찍혀 영구히 유실된다.
            log.warn("Skip notice broadcast: 대상 유저가 0명입니다. pendingNotices={}", pending.size());
            return new Result(0, 0);
        }

        int noticesBroadcasted = 0;
        int notificationsSent = 0;
        for (Notice notice : pending) {
            DeliveryOutcome outcome = broadcastOne(notice, policyId, targets);
            notificationsSent += outcome.sent();

            // 이미 발급된 유저는 dedup 으로 skip 되어 sent=0 이 될 수 있으므로 sent 로는 실패를 판정할 수 없다.
            // 모든 대상이 예외로 실패한 경우에만 완료 처리를 보류하고 다음 실행에서 다시 시도한다.
            if (outcome.failed() == targets.size()) {
                log.warn("Skip marking notice as broadcasted: 전체 발송 실패. noticeId={}, targets={}",
                        notice.getId(), targets.size());
                continue;
            }

            notice.markBroadcasted(Instant.now());
            noticeCommandRepository.save(notice);
            noticesBroadcasted++;
        }

        return new Result(noticesBroadcasted, notificationsSent);
    }

    private DeliveryOutcome broadcastOne(Notice notice, Long policyId, List<OnboardingReminderUserDto> targets) {
        int sent = 0;
        int failed = 0;
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
                failed++;
                log.warn("Notice broadcast delivery failed. noticeId={}, userId={}",
                        notice.getId(), target.userId(), e);
            }
        }
        return new DeliveryOutcome(sent, failed);
    }

    /** 공지 1건에 대한 발송 결과. sent 는 신규 발급 건수, failed 는 예외로 실패한 건수. */
    private record DeliveryOutcome(int sent, int failed) {
    }

    public record Result(int noticesBroadcasted, int notificationsSent) {
    }
}
