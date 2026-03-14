package com.polywave.billservice.security.session;

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
 * bill-service는 user DB를 직접 보지 않고,
 * user-service 내부 API를 통해 현재 세션 유효성을 검증한다.
 *
 * 흐름:
 * 1) bill-service의 JwtAuthenticationFilter가 access token의 userId / sid 추출
 * 2) 현재 요청의 Authorization 헤더를 그대로 user-service로 전달
 * 3) user-service가 현재 유효 세션인지 확인
 * 4) true일 때만 SecurityContext 인증 세팅
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceSessionValidationService implements SessionValidationService {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final RestTemplate restTemplate;

    @Value("${user-service.internal-url:http://user-service:80/users}")
    private String userServiceInternalUrl;

    @Override
    public boolean isValid(HttpServletRequest request, Long userId, String sid) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return false;
        }

        String url = userServiceInternalUrl + "/internal/auth/session/validate?userId=" + userId + "&sid=" + sid;

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

    public record SessionValidationResponse(boolean valid) {
    }
}