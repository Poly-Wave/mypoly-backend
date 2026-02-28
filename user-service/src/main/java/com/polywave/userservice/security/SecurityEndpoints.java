package com.polywave.userservice.security;

public final class SecurityEndpoints {

        private SecurityEndpoints() {
        }

        /** 항상 공개 */
        public static final String[] PUBLIC_ENDPOINTS = {
                        "/", "/error",

                        // SDK 토큰 검증 기반 로그인 엔드포인트
                        "/auth/**",

                        // 약관(로그인 전 접근 가능)
                        "/terms/**",

                        // 닉네임(회원가입 전 기능)
                        "/nicknames/availability", "/nicknames/random",

                        // Swagger (운영에서는 필요 시 막아도 됨)
                        "/swagger-ui/**", "/v3/api-docs/**",

                        // Actuator
                        "/actuator/health/**", "/actuator/info"
        };

        /** Swagger 테스트 생산성용 (dev/local에서만) */
        public static final String[] DEV_ONLY_ENDPOINTS = {
                        "/dev-auth/**"
        };
}
