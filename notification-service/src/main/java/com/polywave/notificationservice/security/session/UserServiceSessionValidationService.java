package com.polywave.notificationservice.security.session;

import com.polywave.security.SessionValidationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * notification-service 는 user DB 를 직접 보지 않고,
 * user-service 내부 API 를 통해 현재 세션 유효성을 검증한다.
 *
 * (bill-service 의 같은 클래스와 동일 패턴)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceSessionValidationService implements SessionValidationService {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Override
    public boolean isValid(HttpServletRequest request, Long userId, String sid) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return false;
        }

        String url = stripTrailingSlash(userServiceUrl)
                + "/internal/auth/session/validate?userId=" + userId + "&sid=" + sid;

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, bearer);

        try {
            ResponseEntity<SessionValidationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    SessionValidationResponse.class
            );

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().valid();
        } catch (RestClientException e) {
            log.warn("user-service 세션 검증 호출 실패: userId={}, url={}", userId, url, e);
            return false;
        }
    }

    private static String stripTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public record SessionValidationResponse(boolean valid) {
    }
}
