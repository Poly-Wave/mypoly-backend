package com.polywave.notificationservice.api.controller;

import com.polywave.notificationservice.api.dto.DevDeliverNotificationRequest;
import com.polywave.notificationservice.api.dto.DevDeliverNotificationResponse;
import com.polywave.notificationservice.api.dto.RunBillStageChangeResponse;
import com.polywave.notificationservice.api.dto.RunBookmarkNoVoteRemindResponse;
import com.polywave.notificationservice.api.dto.RunDailyHomeBroadcastResponse;
import com.polywave.notificationservice.api.dto.RunDailyInterestAgendaResponse;
import com.polywave.notificationservice.api.dto.RunNoticeBroadcastResponse;
import com.polywave.notificationservice.api.dto.RunOnboardingRemindResponse;
import com.polywave.notificationservice.api.spec.DevNotificationApi;
import com.polywave.notificationservice.application.notice.scheduler.NoticeBroadcastScheduler;
import com.polywave.notificationservice.application.notification.scheduler.BillStageChangeNotifier;
import com.polywave.notificationservice.application.notification.scheduler.BookmarkNoVoteReminderScheduler;
import com.polywave.notificationservice.application.notification.scheduler.DailyHomeBroadcastScheduler;
import com.polywave.notificationservice.application.notification.scheduler.DailyInterestAgendaScheduler;
import com.polywave.notificationservice.application.notification.scheduler.OnboardingReminderScheduler;
import com.polywave.notificationservice.application.notification.user.command.service.UserNotificationCommandService;
import com.polywave.notificationservice.domain.notification.UserNotification;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEV/LOCAL 환경에서 Swagger 만으로 알림 발급을 트리거하기 위한 컨트롤러.
 * - notification.dev-trigger.enabled=true 일 때만 빈이 등록된다. (운영에서는 절대 활성화하지 말 것)
 */
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.dev-trigger.enabled", havingValue = "true")
public class DevNotificationController implements DevNotificationApi {

    private final UserNotificationCommandService userNotificationCommandService;
    private final OnboardingReminderScheduler onboardingReminderScheduler;
    private final DailyHomeBroadcastScheduler dailyHomeBroadcastScheduler;
    private final BookmarkNoVoteReminderScheduler bookmarkNoVoteReminderScheduler;
    private final DailyInterestAgendaScheduler dailyInterestAgendaScheduler;
    private final BillStageChangeNotifier billStageChangeNotifier;
    private final NoticeBroadcastScheduler noticeBroadcastScheduler;

    @Override
    public ResponseEntity<DevDeliverNotificationResponse> deliver(DevDeliverNotificationRequest request) {
        Optional<UserNotification> result = userNotificationCommandService.deliverFromPolicy(
                request.userId(),
                request.policyId(),
                request.landingId(),
                request.dedupKey()
        );

        return ResponseEntity.ok(
                result.map(DevDeliverNotificationResponse::delivered)
                        .orElseGet(DevDeliverNotificationResponse::skipped)
        );
    }

    @Override
    public ResponseEntity<RunOnboardingRemindResponse> runOnboardingRemind() {
        OnboardingReminderScheduler.Result result = onboardingReminderScheduler.run();
        return ResponseEntity.ok(RunOnboardingRemindResponse.from(result));
    }

    @Override
    public ResponseEntity<RunDailyHomeBroadcastResponse> runDailyHomeBroadcast() {
        DailyHomeBroadcastScheduler.Result result = dailyHomeBroadcastScheduler.run();
        return ResponseEntity.ok(RunDailyHomeBroadcastResponse.from(result));
    }

    @Override
    public ResponseEntity<RunBookmarkNoVoteRemindResponse> runBookmarkNoVoteRemind() {
        BookmarkNoVoteReminderScheduler.Result result = bookmarkNoVoteReminderScheduler.run();
        return ResponseEntity.ok(RunBookmarkNoVoteRemindResponse.from(result));
    }

    @Override
    public ResponseEntity<RunDailyInterestAgendaResponse> runDailyInterestAgenda() {
        DailyInterestAgendaScheduler.Result result = dailyInterestAgendaScheduler.run();
        return ResponseEntity.ok(RunDailyInterestAgendaResponse.from(result));
    }

    @Override
    public ResponseEntity<RunBillStageChangeResponse> runBillStageChange() {
        BillStageChangeNotifier.Result result = billStageChangeNotifier.run();
        return ResponseEntity.ok(RunBillStageChangeResponse.from(result));
    }

    @Override
    public ResponseEntity<RunNoticeBroadcastResponse> runNoticeBroadcast() {
        NoticeBroadcastScheduler.Result result = noticeBroadcastScheduler.run();
        return ResponseEntity.ok(RunNoticeBroadcastResponse.from(result));
    }
}
