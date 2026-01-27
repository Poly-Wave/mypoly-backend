package com.polywave.userservice.security.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class KakaoUserInfoProvider implements SocialUserInfoProvider {

    @Override
    public String provider() {
        return "kakao";
    }

    @Override
    public SocialUserInfo extract(OAuth2User oAuth2User) {
        return new KakaoUserInfo(oAuth2User);
    }
}
