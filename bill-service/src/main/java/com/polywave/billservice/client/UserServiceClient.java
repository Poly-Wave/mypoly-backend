package com.polywave.billservice.client;

import com.polywave.billservice.client.dto.OnboardingStatusResponse;
import com.polywave.billservice.client.dto.UpdateOnboardingStatusRequest;
import com.polywave.billservice.common.exception.BillErrorCode;
import com.polywave.billservice.common.exception.BillServiceClientException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.function.Supplier;

/**
 * user-service 내부 API 호출용 클라이언트.
 * MSA 구조에서 bill-service → user-service 온보딩 상태 업데이트 시 사용.
 * 현재 요청의 JWT를 Authorization 헤더로 전달하여 user-service에서 본인 확인.
 * 
 * Resilience4j를 사용하여:
 * - 서킷브레이커: user-service 장애 시 빠른 실패
 * - 재시도: 일시적 네트워크 오류 시 자동 재시도
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final RestTemplate restTemplate;
    private final CircuitBreaker userServiceCircuitBreaker;
    private final Retry userServiceRetry;

    @Value("${user-service.url:http://user-service:80/users/}")
    private String userServiceUrl;

    /**
     * 사용자 온보딩 상태 업데이트
     * 현재 요청의 JWT를 user-service로 전달하여 인증 및 본인 확인.
     * 
     * 서킷브레이커와 재시도가 적용됩니다.
     *
     * @param userId           사용자 ID
     * @param onboardingStatus OnBoardingStatus enum 값 (SIGNUP, ONBOARDING,
     *                         CATEGORY, COMPLETE)
     */
    /**
     * 사용자 온보딩 상태 조회
     *
     * @param userId 사용자 ID
     * @return 온보딩 상태 (SIGNUP, ONBOARDING, CATEGORY, COMPLETE)
     */
    public String getOnboardingStatus(Long userId) {
        String url = userServiceUrl + userId + "/onboarding-status";

        HttpHeaders headers = new HttpHeaders();

        // 현재 요청의 JWT를 Authorization 헤더로 전달
        String bearerToken = resolveBearerToken();
        if (bearerToken == null) {
            log.error("JWT 토큰을 찾을 수 없습니다. user-service 호출 실패: userId={}", userId);
            throw new BillServiceClientException(BillErrorCode.MISSING_JWT_TOKEN);
        }

        headers.set(AUTHORIZATION_HEADER, "Bearer " + resolveBearerToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Supplier<String> supplier = () -> {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    OnboardingStatusResponse.class);
            return response.getBody().getOnboardingStatus();
        };

        Supplier<String> retrySupplier = Retry.decorateSupplier(userServiceRetry, supplier);

        try {
            return userServiceCircuitBreaker.executeSupplier(retrySupplier);
        } catch (Exception e) {
            log.error("user-service 온보딩 상태 조회 최종 실패 (서킷브레이커 또는 재시도 한도 초과): userId={}, url={}", userId, url, e);
            throw new BillServiceClientException(BillErrorCode.USER_SERVICE_API_FAILED);
        }
    }

    public void updateOnboardingStatus(Long userId, String onboardingStatus) {
        String url = userServiceUrl + userId + "/onboarding-status";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 현재 요청의 JWT를 Authorization 헤더로 전달
        String bearerToken = resolveBearerToken();
        if (bearerToken == null) {
            log.error("JWT 토큰을 찾을 수 없습니다. user-service 호출 실패: userId={}, status={}", userId, onboardingStatus);
            throw new BillServiceClientException(BillErrorCode.MISSING_JWT_TOKEN);
        }
        headers.set(AUTHORIZATION_HEADER, "Bearer " + bearerToken);

        UpdateOnboardingStatusRequest request = UpdateOnboardingStatusRequest.of(onboardingStatus);
        HttpEntity<UpdateOnboardingStatusRequest> entity = new HttpEntity<>(request, headers);

        // Resilience4j를 사용하여 서킷브레이커와 재시도 적용
        Supplier<Void> supplier = () -> {
            restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);
            log.debug("user-service 온보딩 상태 업데이트 성공: userId={}, status={}", userId, onboardingStatus);
            return null;
        };

        Supplier<Void> retrySupplier = Retry.decorateSupplier(userServiceRetry, supplier);

        try {
            userServiceCircuitBreaker.executeSupplier(retrySupplier);
        } catch (Exception e) {
            log.error("user-service 온보딩 상태 업데이트 최종 실패 (서킷브레이커 또는 재시도 한도 초과): userId={}, status={}, url={}",
                    userId, onboardingStatus, url, e);
            // MSA에서 다른 서비스 호출 실패 시 에러 전파하여 트랜잭션 롤백 유도
            throw new BillServiceClientException(BillErrorCode.USER_SERVICE_API_FAILED);
        }
    }

    private String resolveBearerToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null)
            return null;

        HttpServletRequest request = attrs.getRequest();
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (bearer == null || !bearer.startsWith("Bearer "))
            return null;

        String token = bearer.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
