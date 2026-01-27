package com.polywave.userservice.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

public final class SecurityEndpoints {

    private SecurityEndpoints() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/", "/error", "/login", "/oauth2/**", "/login/oauth2/**",
            "/actuator/health", "/actuator/health/**"
    };

    public static final RequestMatcher PUBLIC_REQUEST_MATCHER =
            new OrRequestMatcher(
                    Arrays.stream(PUBLIC_ENDPOINTS)
                            .map(AntPathRequestMatcher::new)
                            .toArray(RequestMatcher[]::new)
            );
}
