package com.polywave.userservice.security.token;

public record SocialVerifiedUser(
        String provider,
        String providerUserId,
        String profileImageUrl
) {
}
