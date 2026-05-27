package com.polywave.notificationservice.application.notification.scheduler;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.client.BillServiceClient;
import com.polywave.notificationservice.client.dto.BillStageChangeDto;
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
 * 노션 알림 리스트 행 6 — 북마크 의안 단계 변경 알림 (배치 직후 polling).
 *
 *  설계:
 *  - bill-service 의 데이터 수집 배치가 단계 변경을 기록하면, 이 스케줄러가 짧은 주기로
 *    /internal/segments/bookmarked-stage-changes 를 polling 한다.
 *  - 메시지 큐 없이도 사용자 체감 ~10분 이내 알림 도달.
 *
 *  멱등성:
 *  - dedupKey = "BILL_STAGE_CHANGE_NOTIFY:{userId}:{billId}:{toStageCode}"
 *    한 유저가 한 의안의 한 단계 전이를 평생 1회만 받는다.
 *  - cutoff(since) 는 polling 주기보다 여유 있게 잡아도 dedupKey 가 중복을 방어한다.
 *
 *  본문 변수: {안건 제목}, {이전 단계}, {현재 단계}
 *  - fromStageName 이 null (최초 단계 진입) 이면 "(없음)" 으로 치환한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BillStageChangeNotifier {

    /** since cutoff. polling 주기 + 여유. */
    private static final Duration LOOKBACK = Duration.ofHours(1);

    private final BillServiceClient billServiceClient;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;
    private final UserNotificationCommandService userNotificationCommandService;

    @Value("${notification.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /** 기본 10분 주기. */
    @Scheduled(fixedDelayString = "${notification.scheduler.bill-stage-change-fixed-delay-ms:600000}")
    public void runScheduled() {
        if (!schedulerEnabled) {
            log.debug("Skip bill-stage-change notify: notification.scheduler.enabled=false");
            return;
        }
        Result result = run();
        if (result.sent() > 0) {
            log.info("BillStageChangeNotifier done. sent={}", result.sent());
        }
    }

    public Result run() {
        Optional<NotificationPolicy> policyOpt = notificationPolicyCommandRepository
                .findByPolicyKey(SystemNotificationPolicyKey.BILL_STAGE_CHANGE_NOTIFY);
        if (policyOpt.isEmpty()) {
            log.info("Skip: policy not seeded. policyKey={}",
                    SystemNotificationPolicyKey.BILL_STAGE_CHANGE_NOTIFY);
            return new Result(0);
        }
        Long policyId = policyOpt.get().getId();

        Instant since = Instant.now().minus(LOOKBACK);
        List<BillStageChangeDto> changes = billServiceClient.findBookmarkedStageChangesSince(since);
        if (changes.isEmpty()) {
            return new Result(0);
        }

        int sent = 0;
        for (BillStageChangeDto c : changes) {
            try {
                String toCode = c.toStageCode() == null ? "" : c.toStageCode();
                String dedupKey = SystemNotificationPolicyKey.BILL_STAGE_CHANGE_NOTIFY
                        + ":" + c.userId() + ":" + c.billId() + ":" + toCode;
                Map<String, String> vars = Map.of(
                        "안건 제목", c.billTitle() == null ? "" : c.billTitle(),
                        "이전 단계", c.fromStageName() == null ? "(없음)" : c.fromStageName(),
                        "현재 단계", c.toStageName() == null ? "" : c.toStageName()
                );
                Optional<?> result = userNotificationCommandService.deliverFromPolicy(
                        c.userId(), policyId, c.billId(), dedupKey, vars
                );
                if (result.isPresent()) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("Bill stage change delivery failed. userId={}, billId={}",
                        c.userId(), c.billId(), e);
            }
        }
        return new Result(sent);
    }

    public record Result(int sent) {
    }
}
