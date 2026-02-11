package com.polywave.billservice.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error",

            // Swagger (운영에서는 profile로 끄는 게 안전)
            "/swagger-ui/**", "/v3/api-docs/**",

            // Actuator
            "/actuator/health/**", "/actuator/info"
    };
}
