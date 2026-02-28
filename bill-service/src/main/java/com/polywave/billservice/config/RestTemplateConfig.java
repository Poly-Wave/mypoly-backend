package com.polywave.billservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 설정
 * - 타임아웃: 연결 5초, 응답 대기 10초 (내부 서비스 호출에 무리 없도록 여유 둠)
 * - 재시도 로직 (Resilience4j) - 최대 3번 재시도, 500ms 간격
 * - 서킷브레이커 (Resilience4j) - 실패율 50% 초과 시 서킷 오픈
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    private static final String CIRCUIT_BREAKER_NAME = "userService";
    private static final String RETRY_NAME = "userService";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // PATCH 지원: HttpURLConnection(SimpleClientHttpRequestFactory)은 PATCH 미지원 → Apache HttpClient 사용
        // Apache HttpClient 5: ConnectionConfig로 연결 타임아웃, RequestConfig로 응답 타임아웃 설정
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .build();

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
                .requestFactory(() -> factory)
                .build();
    }

    @Bean
    public CircuitBreaker userServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 실패율 50% 초과 시 서킷 오픈
                .waitDurationInOpenState(Duration.ofSeconds(10)) // 서킷 오픈 후 10초 대기
                .slidingWindowSize(10) // 최근 10개 요청 기준
                .minimumNumberOfCalls(5) // 최소 5번 호출 후 통계 시작
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        return registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
    }

    @Bean
    public Retry userServiceRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // 최대 3번 재시도
                .waitDuration(Duration.ofMillis(500)) // 재시도 간격 500ms
                .retryOnException(ex -> ex instanceof RestClientException) // RestClientException 발생 시 재시도
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        return registry.retry(RETRY_NAME);
    }
}
