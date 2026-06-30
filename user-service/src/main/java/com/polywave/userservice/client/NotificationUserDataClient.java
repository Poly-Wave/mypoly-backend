package com.polywave.userservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 회원 탈퇴 시 notification-service 의 사용자 알림 데이터를 삭제하는 internal 호출 클라이언트.
 *
 * <p>notification-service 는 X-Admin-Api-Key 헤더로 가드된다(bill-service 의 X-Internal-Api-Key 와 다름).
 * 삭제 실패 시 예외를 전파하여 탈퇴 트랜잭션이 롤백되게 한다(삭제는 멱등).
 */
@Slf4j
@Component
public class NotificationUserDataClient {

    private static final String DELETE_PATH = "/internal/user-notifications/{userId}";

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final String adminApiKey;

    public NotificationUserDataClient(
            RestTemplate restTemplate,
            @Value("${notification-service.url}") String notificationServiceUrl,
            @Value("${notification-service.admin-api.key:}") String adminApiKey
    ) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = stripTrailingSlash(notificationServiceUrl);
        this.adminApiKey = adminApiKey;
    }

    public void deleteUserNotifications(Long userId) {
        if (adminApiKey == null || adminApiKey.isBlank()) {
            throw new IllegalStateException(
                    "notification-service.admin-api.key 가 설정되지 않아 탈퇴(알림 삭제)를 진행할 수 없습니다.");
        }

        String url = UriComponentsBuilder.fromHttpUrl(notificationServiceUrl + DELETE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Admin-Api-Key", adminApiKey);

        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        log.info("notification-service 사용자 알림 삭제 완료 userId={}", userId);
    }

    private static String stripTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
