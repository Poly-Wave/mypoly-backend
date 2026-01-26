package com.polywave.userservice.api.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public final class KakaoUserInfo implements SocialUserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(OAuth2User oAuth2User) {
        this.attributes = oAuth2User.getAttributes();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderUserId() {
        Object idObj = attributes.get("id");
        return idObj == null ? null : String.valueOf(idObj);
    }

    @Override
    public String getNickname() {
        // 권장: kakao_account.profile.nickname
        String nick = getString(getMap(getMap(attributes, "kakao_account"), "profile"), "nickname");
        if (nick != null) return nick;

        // 구형/설정에 따라: properties.nickname
        return getString(getMap(attributes, "properties"), "nickname");
    }

    @Override
    public String getProfileImageUrl() {
        // 권장: kakao_account.profile.profile_image_url
        String url = getString(getMap(getMap(attributes, "kakao_account"), "profile"), "profile_image_url");
        if (url != null) return url;

        // 구형/설정에 따라: properties.profile_image
        url = getString(getMap(attributes, "properties"), "profile_image");
        if (url != null) return url;

        // 혹시 키가 다를 때: kakao_account.profile.profile_image
        return getString(getMap(getMap(attributes, "kakao_account"), "profile"), "profile_image");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> src, String key) {
        if (src == null) return null;
        Object v = src.get(key);
        if (v instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return null;
    }

    private static String getString(Map<String, Object> src, String key) {
        if (src == null) return null;
        Object v = src.get(key);
        return v == null ? null : String.valueOf(v);
    }
}
