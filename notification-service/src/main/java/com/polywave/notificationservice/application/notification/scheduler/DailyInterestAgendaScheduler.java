package com.polywave.notificationservice.application.notification.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.BillServiceClient;
import com.polywave.notificationservice.client.UserServiceClient;
import com.polywave.notificationservice.client.dto.UserInterestAgendaCountDto;
import com.polywave.notificationservice.client.dto.UserNicknameDto;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 노션 알림 리스트 행 3 — 매일 09:00 KST 관심 카테고리 매칭 신규 안건 알림.
 *
 *  대상: 사용자의 관심 카테고리에 매칭되는 어제(KST) 등록 안건이 존재하는 유저.
 *  본문 변수: {별명}, {8} (= 매칭 안건 개수)
 *  랜딩: NOTIFICATION_LIST (= 안건 메인_관심 주제 sorting 리스트 화면, 앱이 그쪽으로 라우팅)
 *
 *  멱등성:
 *  - dedupKey = "{POLICY_KEY}:{userId}:{date(KST)}" → 같은 날 중복 발급 방지.
 *
 *  데이터 소스:
 *  - bill-service /internal/segments/interest-matched-counts (관심 매칭 카운트)
 *  - user-service  /internal/lookup/by-ids (닉네임 일괄 조회)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyInterestAgendaScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final BillServiceClient billServiceClient;
    private final UserServiceClient userServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /** 매일 09:00 KST. */
    @Scheduled(cron = "${notification.scheduler.daily-interest-agenda-cron:0 0 9 * * *}", zone = "Asia/Seoul")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip scheduled daily interest-agenda: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        log.info("DailyInterestAgendaScheduler done. sent={}", result.sent());
    }

    public Result run() {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository
                .findByPolicyKey(SystemNotificationPolicyKey.HOME_NEW_INTEREST_AGENDA_DAILY);
        if (policyOpt.isEmpty()) {
            log.info("Skip: policy not seeded. policyKey={}",
                    SystemNotificationPolicyKey.HOME_NEW_INTEREST_AGENDA_DAILY);
            return new Result(0);
        }
        Long policyId = policyOpt.get().getId();

        // [어제 KST 00:00, 오늘 KST 00:00) 범위에 first_collected_at 이 들어간 안건들이 대상.
        LocalDate today = LocalDate.now(KST);
        Instant from = today.minusDays(1).atStartOfDay(KST).toInstant();
        Instant to = today.atStartOfDay(KST).toInstant();
        String dateKey = today.toString();

        List<UserInterestAgendaCountDto> counts =
                billServiceClient.findInterestMatchedCountsBetween(from, to);
        if (counts.isEmpty()) {
            return new Result(0);
        }

        // 닉네임 일괄 조회
        Set<Long> userIds = counts.stream()
                .map(UserInterestAgendaCountDto::userId)
                .collect(Collectors.toSet());
        Map<Long, String> nicknameByUserId = new HashMap<>();
        for (UserNicknameDto u : userServiceClient.findNicknamesByIds(userIds)) {
            nicknameByUserId.put(u.userId(), u.nickname() == null ? "" : u.nickname());
        }

        int sent = 0;
        for (UserInterestAgendaCountDto c : counts) {
            if (c.count() <= 0) {
                continue;
            }
            try {
                String dedupKey = SystemNotificationPolicyKey.HOME_NEW_INTEREST_AGENDA_DAILY
                        + ":" + c.userId() + ":" + dateKey;
                Map<String, String> vars = Map.of(
                        "별명", nicknameByUserId.getOrDefault(c.userId(), ""),
                        "개수", String.valueOf(c.count())
                );
                Optional<?> result = userNotificationCommandService.deliverFromPolicy(
                        c.userId(), policyId, null, dedupKey, vars
                );
                if (result.isPresent()) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("Daily interest-agenda delivery failed. userId={}", c.userId(), e);
            }
        }
        return new Result(sent);
    }

    public record Result(int sent) {
    }
}
