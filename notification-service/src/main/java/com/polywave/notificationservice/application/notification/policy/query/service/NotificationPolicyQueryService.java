package com.polywave.notificationservice.application.notification.policy.query.service;

import com.polywave.notificationservice.application.notification.policy.query.result.NotificationPolicyResult;
import com.polywave.notificationservice.common.exception.NotificationPolicyNotFoundException;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import com.polywave.notificationservice.repository.query.NotificationPolicyQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationPolicyQueryService {

    private final NotificationPolicyQueryRepository notificationPolicyQueryRepository;
    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;

    public PolicyPage findPolicies(
            NotificationPolicyStatus status,
            NotificationChannel channel,
            NotificationCategory category,
            Pageable pageable
    ) {
        List<NotificationPolicy> rows = notificationPolicyQueryRepository.findPolicies(
                status, channel, category, pageable
        );

        int pageSize = pageable.getPageSize();
        boolean hasNext = rows.size() > pageSize;
        List<NotificationPolicyResult> content = rows.stream()
                .limit(pageSize)
                .map(NotificationPolicyResult::from)
                .toList();

        return new PolicyPage(content, pageable.getPageNumber(), pageSize, hasNext);
    }

    public NotificationPolicyResult findById(Long policyId) {
        return notificationPolicyCommandRepository.findById(policyId)
                .map(NotificationPolicyResult::from)
                .orElseThrow(NotificationPolicyNotFoundException::new);
    }

    public record PolicyPage(
            List<NotificationPolicyResult> content,
            int page,
            int size,
            boolean hasNext
    ) {
    }
}
