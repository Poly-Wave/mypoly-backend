package com.polywave.userservice.api.dto;

public record SocialUserDto(
    Long userId,
    String provider,
    String providerUserId,
    String nickname,
    String profileImageUrl
) {}
