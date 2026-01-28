package com.polywave.userservice.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error", "/oauth2/**", "/login/oauth2/**",

            // 소셜 로그인 시작(공식)
            "/auth/**",
            
            //Actuator 엔드포인트
            "/actuator/health", "/actuator/health/**",

            //Swagger(OpenApi) API
            "/v3/api-docs", "/v3/api-docs/**",
            "/swagger-ui.html", "/swagger-ui/**"
    };

    public static final RequestMatcher PUBLIC_REQUEST_MATCHER =
            new OrRequestMatcher(
                    Arrays.stream(PUBLIC_ENDPOINTS)
                            .map(AntPathRequestMatcher::new)
                            .toArray(RequestMatcher[]::new)
            );
}
