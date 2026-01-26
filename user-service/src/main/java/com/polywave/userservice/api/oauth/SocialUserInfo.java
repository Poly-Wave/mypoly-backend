package com.polywave.userservice.api.oauth;

public interface SocialUserInfo {
    String getProvider();
    String getProviderUserId();
    String getNickname();
    String getProfileImageUrl();
}
