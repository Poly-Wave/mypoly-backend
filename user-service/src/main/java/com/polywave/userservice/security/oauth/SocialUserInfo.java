package com.polywave.userservice.security.oauth;

public interface SocialUserInfo {
    String getProvider();
    String getProviderUserId();
    String getNickname();
    String getProfileImageUrl();
}
