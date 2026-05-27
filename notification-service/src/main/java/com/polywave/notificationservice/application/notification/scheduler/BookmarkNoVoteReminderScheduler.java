package com.polywave.notificationservice.application.notification.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.BillServiceClient;
import com.polywave.notificationservice.client.dto.BookmarkedUnvotedBillDto;
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
 * 노션 알림 리스트 행 7 — 북마크 D+1 미투표 리마인더 스케줄러.
 *
 *  대상: 북마크 저장 시각이 24시간 전 이전이고, 그 의안에 아직 투표하지 않은 (user, bill) 쌍.
 *  본문 변수: {안건 제목} → bill.officialTitle.
 *  랜딩: BILL_DETAIL + landingId = billId.
 *
 * 멱등성:
 *  - dedupKey = "{POLICY_KEY}:{userId}:{billId}" — 한 유저가 한 의안에 평생 1회.
 *  - 사용자가 투표를 안 한 채로 며칠이 지나도 재발송되지 않는다 (의도된 동작 — 알림 피로 방지).
 *
 * 데이터 소스:
 *  - bill-service /internal/segments/bookmarked-unvoted 호출. notification-service 는 bookmark/vote 데이터를 직접 보지 않는다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookmarkNoVoteReminderScheduler {

    private static final Duration D_PLUS_1 = Duration.ofDays(1);

    private final BillServiceClient billServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "${notification.scheduler.bookmark-no-vote-remind-cron:0 0 12 * * *}", zone = "Asia/Seoul")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip scheduled bookmark-no-vote reminder: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        log.info("BookmarkNoVoteReminderScheduler done. sent={}", result.sent());
    }

    public Result run() {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository
                .findByPolicyKey(SystemNotificationPolicyKey.BILL_BOOKMARK_NO_VOTE_REMIND_D1);
        if (policyOpt.isEmpty()) {
            log.info("Skip remind: policy not seeded. policyKey={}",
                    SystemNotificationPolicyKey.BILL_BOOKMARK_NO_VOTE_REMIND_D1);
            return new Result(0);
        }
        Long policyId = policyOpt.get().getId();

        Instant cutoff = Instant.now().minus(D_PLUS_1);
        List<BookmarkedUnvotedBillDto> targets = billServiceClient.findBookmarkedUnvotedBefore(cutoff);

        int sent = 0;
        for (BookmarkedUnvotedBillDto target : targets) {
            try {
                String dedupKey = SystemNotificationPolicyKey.BILL_BOOKMARK_NO_VOTE_REMIND_D1
                        + ":" + target.userId() + ":" + target.billId();
                Map<String, String> vars = Map.of("안건 제목",
                        target.billTitle() == null ? "" : target.billTitle());
                Optional<?> result = userNotificationCommandService.deliverFromPolicy(
                        target.userId(),
                        policyId,
                        target.billId(),
                        dedupKey,
                        vars
                );
                if (result.isPresent()) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("Bookmark no-vote remind delivery failed. userId={}, billId={}",
                        target.userId(), target.billId(), e);
            }
        }
        return new Result(sent);
    }

    public record Result(int sent) {
    }
}
