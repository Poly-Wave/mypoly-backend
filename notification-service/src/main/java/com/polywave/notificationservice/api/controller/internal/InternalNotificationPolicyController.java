package com.polywave.notificationservice.api.controller.internal;

import com.polywave.notificationservice.api.dto.NotificationPolicyListResponse;
import com.polywave.notificationservice.api.dto.NotificationPolicyRequest;
import com.polywave.notificationservice.api.dto.NotificationPolicyResponse;
import com.polywave.notificationservice.api.dto.NotificationPolicyStatusUpdateRequest;
import com.polywave.notificationservice.api.spec.InternalNotificationPolicyApi;
import com.polywave.notificationservice.application.notification.policy.command.service.NotificationPolicyCommandService;
import com.polywave.notificationservice.application.notification.policy.query.service.NotificationPolicyQueryService;
import com.polywave.notificationservice.application.notification.policy.query.service.NotificationPolicyQueryService.PolicyPage;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.application.notification.policy.query.result.NotificationPolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자/내부용 알림 정책 관리 API.
 *
 * - 현재 프로젝트에 관리자 전용 API 구조가 없으므로,
 *   기존 internal 패턴(InternalAuthSessionController)에 맞춰 /internal 경로로 우선 구성한다.
 * - 추후 관리자 화면을 붙일 때 동일 컨트롤러를 그대로 활용할 수 있다.
 */
@RestController
@RequiredArgsConstructor
public class InternalNotificationPolicyController implements InternalNotificationPolicyApi {

    private final NotificationPolicyCommandService notificationPolicyCommandService;
    private final NotificationPolicyQueryService notificationPolicyQueryService;

    @Override
    public ResponseEntity<NotificationPolicyListResponse> getPolicies(
            NotificationPolicyStatus status,
            NotificationChannel channel,
            NotificationCategory category,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PolicyPage policies = notificationPolicyQueryService.findPolicies(status, channel, category, pageable);
        return ResponseEntity.ok(NotificationPolicyListResponse.from(policies));
    }

    @Override
    public ResponseEntity<NotificationPolicyResponse> getPolicy(Long policyId) {
        NotificationPolicyResult result = notificationPolicyQueryService.findById(policyId);
        return ResponseEntity.ok(NotificationPolicyResponse.from(result));
    }

    @Override
    public ResponseEntity<NotificationPolicyResponse> createPolicy(NotificationPolicyRequest request) {
        NotificationPolicy policy = notificationPolicyCommandService.create(request.toCommand());
        return ResponseEntity.ok(NotificationPolicyResponse.from(NotificationPolicyResult.from(policy)));
    }

    @Override
    public ResponseEntity<NotificationPolicyResponse> updatePolicy(Long policyId, NotificationPolicyRequest request) {
        NotificationPolicy policy = notificationPolicyCommandService.update(policyId, request.toCommand());
        return ResponseEntity.ok(NotificationPolicyResponse.from(NotificationPolicyResult.from(policy)));
    }

    @Override
    public ResponseEntity<NotificationPolicyResponse> updatePolicyStatus(
            Long policyId,
            NotificationPolicyStatusUpdateRequest request
    ) {
        NotificationPolicy policy = notificationPolicyCommandService.changeStatus(policyId, request.status());
        return ResponseEntity.ok(NotificationPolicyResponse.from(NotificationPolicyResult.from(policy)));
    }
}
