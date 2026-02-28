package com.polywave.userservice.application.auth.result;

public record SocialLoginResult(
    Long userId,
    String provider,
    String providerUserId,
    String nickname,
    String profileImageUrl,
    String jwt) {
}
