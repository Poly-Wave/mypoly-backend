package com.polywave.notificationservice.repository.query;

import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NotificationPolicyQueryRepository {

    List<NotificationPolicy> findPolicies(
            NotificationPolicyStatus status,
            NotificationChannel channel,
            NotificationCategory category,
            Pageable pageable
    );
}
