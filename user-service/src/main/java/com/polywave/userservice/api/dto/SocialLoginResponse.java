package com.polywave.userservice.api.dto;

public record SocialLoginResponse(
        Long userId,
        String provider,
        String providerUserId,
        String nickname,
        String profileImageUrl,
        String jwt
) {
}
