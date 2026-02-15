package com.polywave.userservice.security.token.kakao;

import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiClient {

    private final RestTemplate restTemplate;

    public KakaoApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .rootUri("https://kapi.kakao.com")
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public Map<String, Object> tokenInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    "/v1/user/access_token_info",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            return resp.getBody();
        } catch (RestClientResponseException e) {
            throw new InvalidSocialTokenException("카카오 토큰 검증 실패(access_token_info): " + e.getStatusCode());
        } catch (Exception e) {
            throw new InvalidSocialTokenException("카카오 토큰 검증 실패(access_token_info)");
        }
    }

    public Map<String, Object> userMe(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    "/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            return resp.getBody();
        } catch (RestClientResponseException e) {
            throw new InvalidSocialTokenException("카카오 사용자 조회 실패(user/me): " + e.getStatusCode());
        } catch (Exception e) {
            throw new InvalidSocialTokenException("카카오 사용자 조회 실패(user/me)");
        }
    }
}
