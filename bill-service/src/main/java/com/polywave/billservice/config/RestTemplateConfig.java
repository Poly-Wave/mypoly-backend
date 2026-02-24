package com.polywave.billservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * RestTemplate 설정
 * - 타임아웃 설정 (연결: 3초, 읽기: 5초)
 * - 재시도 로직 (Resilience4j) - 최대 3번 재시도
 * - 서킷브레이커 (Resilience4j) - 실패율 50% 초과 시 서킷 오픈
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    private static final String CIRCUIT_BREAKER_NAME = "userService";
    private static final String RETRY_NAME = "userService";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 타임아웃 설정
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        ((SimpleClientHttpRequestFactory) factory).setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        ((SimpleClientHttpRequestFactory) factory).setReadTimeout((int) Duration.ofSeconds(5).toMillis());

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
