package com.polywave.notificationservice.application.notification.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.segment.OnboardingReminderTarget;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.UserServiceClient;
import com.polywave.notificationservice.client.UserServiceClient.SegmentType;
import com.polywave.notificationservice.client.dto.OnboardingReminderUserDto;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import java.time.Duration;
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
 * 노션 알림 리스트 행 1, 2 — 온보딩 리마인더 스케줄러.
 *
 *  행 1: 별명 설정 D+1 + 관심 주제 미선택 유저
 *  행 2: 관심 주제 선택 D+1 + 추가 정보 미입력 유저
 *
 * 멱등성 보장:
 *  - dedupKey = "{POLICY_KEY}:{userId}" — 한 유저가 같은 알림을 평생 1번만 받는다.
 *  - 정책 본문/타이틀을 바꾸어 같은 의미의 알림을 다시 보내고 싶다면 새로운 policy_key 로 정책을 만들면 된다.
 *
 * 데이터 소스:
 *  - 사용자 마일스톤 시각은 user-service 가 소유. UserServiceClient 가 internal API 를 호출해 세그먼트를 가져온다.
 *  - 알림 도메인(정책/이력/푸시)은 notification-service 가 소유.
 *
 * 운영 안전장치:
 *  - notification.scheduler.enabled=true 일 때만 @Scheduled 실행. (운영 반영 전에 끌 수 있음)
 *  - 정책이 시드되어 있지 않거나 status != ACTIVE 면 자동 skip.
 *  - 정책 lookup 은 policy_key 기반 → 정책 ID 가 바뀌어도 코드 영향 없음.
 *  - run() 은 public 으로 노출되어 DEV 트리거에서 수동 호출이 가능하다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OnboardingReminderScheduler {

    private static final Duration D_PLUS_1 = Duration.ofDays(1);

    private final UserServiceClient userServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "${notification.scheduler.onboarding-remind-cron:0 0 12 * * *}", zone = "Asia/Seoul")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip scheduled onboarding reminder: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        log.info("OnboardingReminderScheduler done. nicknameRemindSent={}, categoryRemindSent={}",
                result.nicknameRemindSent(), result.categoryRemindSent());
    }

    /**
     * 핵심 실행 로직. DEV 트리거에서도 직접 호출하여 검증 가능하다.
     */
    public Result run() {
        Instant cutoff = Instant.now().minus(D_PLUS_1);
        int nicknameSent = runRemind(
                SystemNotificationPolicyKey.ONBOARDING_NICKNAME_REMIND_D1,
                toTargets(userServiceClient.findOnboardingReminderTargets(SegmentType.NICKNAME, cutoff))
        );
        int categorySent = runRemind(
                SystemNotificationPolicyKey.ONBOARDING_CATEGORY_REMIND_D1,
                toTargets(userServiceClient.findOnboardingReminderTargets(SegmentType.CATEGORY, cutoff))
        );
        return new Result(nicknameSent, categorySent);
    }

    private List<OnboardingReminderTarget> toTargets(List<OnboardingReminderUserDto> dtos) {
        return dtos.stream()
                .map(d -> new OnboardingReminderTarget(d.userId(), d.nickname() == null ? "" : d.nickname()))
                .toList();
    }

    private int runRemind(String policyKey, List<OnboardingReminderTarget> targets) {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository.findByPolicyKey(policyKey);
        if (policyOpt.isEmpty()) {
            log.info("Skip remind: policy not seeded. policyKey={}", policyKey);
            return 0;
        }
        Long policyId = policyOpt.get().getId();

        int sent = 0;
        for (OnboardingReminderTarget target : targets) {
            // user 단위 try-catch — 한 user 의 unique violation 등으로 batch 전체가 멈추는 것을 막는다.
            try {
                String dedupKey = policyKey + ":" + target.userId();
                Map<String, String> vars = Map.of("별명", target.nickname());
                Optional<?> result = userNotificationCommandService.deliverFromPolicy(
                        target.userId(),
                        policyId,
                        null,
                        dedupKey,
                        vars
                );
                if (result.isPresent()) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("Onboarding remind delivery failed. policyKey={}, userId={}", policyKey, target.userId(), e);
            }
        }
        return sent;
    }

    public record Result(int nicknameRemindSent, int categoryRemindSent) {
    }
}
