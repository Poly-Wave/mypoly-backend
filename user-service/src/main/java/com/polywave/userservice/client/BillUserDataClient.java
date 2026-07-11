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
 * 회원 탈퇴 시 bill-service 의 사용자 파생 데이터(북마크/관심사/조회 기록)를 삭제하는 internal 호출 클라이언트.
 *
 * <p>표결(user_bill_votes)은 삭제 대상이 아니며 bill-service 측에서 보존한다.
 *
 * <p>조회용 segment 클라이언트와 달리 삭제는 실패를 삼키지 않고 예외를 전파한다.
 * 호출이 실패하면 탈퇴 트랜잭션이 롤백되어 사용자가 재시도할 수 있다(삭제는 멱등).
 */
@Slf4j
@Component
public class BillUserDataClient {

    private static final String DELETE_PATH = "/internal/users/{userId}/bill-data";

    private final RestTemplate restTemplate;
    private final String billServiceUrl;
    private final String internalApiKey;

    public BillUserDataClient(
            RestTemplate restTemplate,
            @Value("${bill-service.url}") String billServiceUrl,
            @Value("${bill-service.internal-api.key:}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.billServiceUrl = stripTrailingSlash(billServiceUrl);
        this.internalApiKey = internalApiKey;
    }

    public void deleteUserBillData(Long userId) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            throw new IllegalStateException(
                    "bill-service.internal-api.key 가 설정되지 않아 탈퇴(bill 데이터 삭제)를 진행할 수 없습니다.");
        }

        String url = UriComponentsBuilder.fromHttpUrl(billServiceUrl + DELETE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);

        // 2xx 가 아니면 RestTemplate 가 예외를 던져 호출부(탈퇴 트랜잭션)로 전파된다.
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        log.info("bill-service 사용자 데이터 삭제 완료 userId={}", userId);
    }

    private static String stripTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
