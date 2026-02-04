package com.polywave.userservice.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error",
            "/oauth2/**", "/login/oauth2/**",

            // 소셜 로그인 시작(공식)
            "/auth/**",

            // Actuator
            "/actuator/health", "/actuator/health/**",

            // Swagger(OpenApi)
            "/v3/api-docs", "/v3/api-docs/**",
            "/swagger-ui.html", "/swagger-ui/**"
    };
}
