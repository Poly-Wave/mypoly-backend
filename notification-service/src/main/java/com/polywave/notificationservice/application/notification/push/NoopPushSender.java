package com.polywave.notificationservice.application.notification.push;

import com.polywave.notificationservice.domain.notification.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 기본 PushSender 구현체.
 *
 * - 실제 외부 푸시(FCM/APNS) 연동을 하지 않고 로그만 남긴다.
 * - 추후 FCM 구현체가 Bean 으로 등록되면 자동으로 그쪽이 우선된다.
 */
@Slf4j
@Component
@ConditionalOnMissingBean(value = PushSender.class, ignored = NoopPushSender.class)
public class NoopPushSender implements PushSender {

    @Override
    public void send(UserNotification notification) {
        if (notification == null) {
            return;
        }
        log.info(
                "[NoopPushSender] skip real push send. userId={}, category={}, title={}, landingType={}",
                notification.getUserId(),
                notification.getCategory(),
                notification.getTitle(),
                notification.getLandingType()
        );
    }
}
