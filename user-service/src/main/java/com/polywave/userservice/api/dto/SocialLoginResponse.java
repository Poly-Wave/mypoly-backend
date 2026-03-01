package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 성공 응답(JWT 포함)", requiredProperties = {"userId", "provider", "providerUserId", "jwt"})
public record SocialLoginResponse(
        @Schema(description = "MyPoly 내부 유저 ID", example = "123")
        Long userId,

        @Schema(description = "OAuth2 Provider", example = "kakao")
        String provider,

        @Schema(description = "Provider 사용자 식별 값", example = "3123123")
        String providerUserId,

        @Schema(description = "현재 닉네임 (미설정 시 null)", example = "당근도사", nullable = true)
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
        String profileImageUrl,

        @Schema(
                description = "Access Token(JWT). 다른 서비스(bill-service 등) 호출 시 Authorization 헤더로 전달",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String jwt
) {
}
