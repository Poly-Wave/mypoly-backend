package com.polywave.userservice.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error",
            "/oauth2/**", "/login/oauth2/**",

            // 소셜 로그인 시작(공식)
            "/auth/**",

            // 약관(로그인 전 접근 가능해야 함)
            "/terms/**",

            // Swagger (운영에서는 필요 시 막아도 됨)
            "/swagger-ui/**", "/v3/api-docs/**",

            // Actuator
            "/actuator/health/**", "/actuator/info"
    };
}
