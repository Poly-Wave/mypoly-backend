package com.polywave.userservice.security.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface SocialUserInfoProvider {
    String provider();

    SocialUserInfo extract(OAuth2User oAuth2User);
}
