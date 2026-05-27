package com.polywave.notificationservice.repository.command;

import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPolicyCommandRepository extends JpaRepository<NotificationPolicy, Long> {

    Optional<NotificationPolicy> findByPolicyKey(String policyKey);
}
