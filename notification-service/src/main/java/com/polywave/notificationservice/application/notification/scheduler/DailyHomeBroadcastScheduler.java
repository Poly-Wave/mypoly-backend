package com.polywave.notificationservice.application.notification.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.UserServiceClient;
import com.polywave.notificationservice.client.dto.OnboardingReminderUserDto;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 노션 알림 리스트 행 4, 5 — 매일 18:00 KST 홈 broadcast 스케줄러.
 *
 *  행 4: 요즘 핫한 안건 모아보기  (정책 key = HOME_TRENDING_AGENDA_DAILY)
 *  행 5: 최근 30일 인기 안건 모아보기  (정책 key = HOME_RECENT_30D_POPULAR_DAILY)
 *
 * 대상:
 *  - profile_completed_at != null (= 온보딩 완료) 유저 전원.
 *  - user-service /internal/segments/onboarding-completed 호출로 가져옴.
 *
 * 멱등성:
 *  - dedupKey = "{POLICY_KEY}:{userId}:{date(KST)}" — 같은 날 중복 발급 방지.
 *  - 어제 발급된 알림은 오늘 dedupKey 가 달라 정상 발급된다.
 *
 * 운영 안전장치:
 *  - notification.scheduler.enabled=true 일 때만 @Scheduled 실행.
 *  - 정책이 시드되어 있지 않거나 status != ACTIVE 면 자동 skip.
 *  - bill-service 와 무관. notification-service 자체 cron 으로 동작.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyHomeBroadcastScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserServiceClient userServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /** 매일 18:00 KST. */
    @Scheduled(cron = "${notification.scheduler.daily-home-broadcast-cron:0 0 18 * * *}", zone = "Asia/Seoul")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip scheduled daily home broadcast: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        log.info("DailyHomeBroadcastScheduler done. trendingSent={}, recent30dPopularSent={}",
                result.trendingSent(), result.recent30dPopularSent());
    }

    public Result run() {
        // 두 정책 모두 같은 segment 를 사용하므로 user-service 호출은 1번만.
        List<OnboardingReminderUserDto> targets = userServiceClient.findOnboardingCompletedUsers();
        String dateKey = LocalDate.now(KST).toString();

        int trendingSent = runBroadcast(
                SystemNotificationPolicyKey.HOME_TRENDING_AGENDA_DAILY, targets, dateKey
        );
        int recent30dPopularSent = runBroadcast(
                SystemNotificationPolicyKey.HOME_RECENT_30D_POPULAR_DAILY, targets, dateKey
        );
        return new Result(trendingSent, recent30dPopularSent);
    }

    private int runBroadcast(String policyKey, List<OnboardingReminderUserDto> targets, String dateKey) {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository.findByPolicyKey(policyKey);
        if (policyOpt.isEmpty()) {
            log.info("Skip broadcast: policy not seeded. policyKey={}", policyKey);
            return 0;
        }
        Long policyId = policyOpt.get().getId();

        int sent = 0;
        for (OnboardingReminderUserDto target : targets) {
            try {
                String dedupKey = policyKey + ":" + target.userId() + ":" + dateKey;
                Map<String, String> vars = Map.of("별명", target.nickname() == null ? "" : target.nickname());
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
                log.warn("Home broadcast delivery failed. policyKey={}, userId={}", policyKey, target.userId(), e);
            }
        }
        return sent;
    }

    public record Result(int trendingSent, int recent30dPopularSent) {
    }
}
