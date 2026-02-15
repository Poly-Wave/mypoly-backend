package com.polywave.userservice.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    /** 항상 공개 */
    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error",

            // SDK 토큰 검증 기반 로그인 엔드포인트
            "/auth/**",

            // 약관(로그인 전 접근 가능)
            "/terms/**",

            // Swagger (운영에서는 필요 시 막아도 됨)
            "/swagger-ui/**", "/v3/api-docs/**",

            // Actuator
            "/actuator/health/**", "/actuator/info"
    };

    /** oauth2 리다이렉트 플로우를 dev/local에서만 열고 싶을 때 */
    public static final String[] OAUTH2_ENDPOINTS = {
            "/oauth2/**", "/login/oauth2/**",
            "/auth/redirect/**"
    };

    /** Swagger 테스트 생산성용 (dev/local에서만) */
    public static final String[] DEV_ONLY_ENDPOINTS = {
            "/dev-auth/**"
    };
}
