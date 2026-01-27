package com.polywave.userservice.security.oauth;

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
        String nick = getString(getMap(getMap(attributes, "kakao_account"), "profile"), "nickname");
        if (nick != null) return nick;

        return getString(getMap(attributes, "properties"), "nickname");
    }

    @Override
    public String getProfileImageUrl() {
        String url = getString(getMap(getMap(attributes, "kakao_account"), "profile"), "profile_image_url");
        if (url != null) return url;

        url = getString(getMap(attributes, "properties"), "profile_image");
        if (url != null) return url;

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
