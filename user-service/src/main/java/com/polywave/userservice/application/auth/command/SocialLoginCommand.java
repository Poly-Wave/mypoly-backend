package com.polywave.userservice.application.auth.command;

public record SocialLoginCommand(
        String provider,
        String providerUserId,
        String nickname,
        String profileImageUrl
) {
}
