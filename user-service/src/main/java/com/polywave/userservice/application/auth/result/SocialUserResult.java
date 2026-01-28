package com.polywave.userservice.application.auth.result;

public record SocialUserResult(
        Long userId,
        String provider,
        String providerUserId,
        String nickname,
        String profileImageUrl
) {
}
