package com.polywave.notificationservice.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    /** 항상 공개 */
    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error",

            // 알림 정책 관리: AdminApiKeyFilter 가 별도 가드, Spring Security 단에서는 공개.
            "/internal/notification-policies/**",

            // Swagger
            "/swagger-ui/**", "/v3/api-docs/**",

            // Actuator
            "/actuator/health/**", "/actuator/info"
    };

    /** DEV/LOCAL 전용 */
    public static final String[] DEV_ONLY_ENDPOINTS = {
            "/dev-notifications/**"
    };
}
