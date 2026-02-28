package com.polywave.userservice.security.token.kakao;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import com.polywave.userservice.security.token.SocialTokenVerifier;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoAccessTokenVerifier implements SocialTokenVerifier {

    private final KakaoApiClient kakaoApiClient;
    private final String expectedAppId; // 옵션 (aud 비슷한 개념)

    public KakaoAccessTokenVerifier(
            KakaoApiClient kakaoApiClient,
            @Value("${social.kakao.expected-app-id:}") String expectedAppId) {
        this.kakaoApiClient = kakaoApiClient;
        this.expectedAppId = expectedAppId;
    }

    @Override
    public String provider() {
        return "kakao";
    }

    @Override
    public boolean supports(SocialTokenType tokenType) {
        return tokenType == SocialTokenType.ACCESS_TOKEN;
    }

    @Override
    public SocialVerifiedUser verify(String token, SocialTokenType tokenType) {
        // 1) access_token_info로 만료/앱 식별 검증
        Map<String, Object> tokenInfo = kakaoApiClient.tokenInfo(token);

        Long expiresIn = getLong(tokenInfo, "expires_in");
        if (expiresIn == null || expiresIn <= 0) {
            throw new InvalidSocialTokenException();
        }

        // app_id 체크(옵션): 값이 설정되어 있으면 강제 검증
        Long appId = getLong(tokenInfo, "app_id");
        if (expectedAppId != null && !expectedAppId.isBlank()) {
            if (appId == null || !expectedAppId.equals(String.valueOf(appId))) {
                throw new InvalidSocialTokenException();
            }
        }

        // 2) user/me로 실제 사용자 식별자 및 프로필 조회
        Map<String, Object> me = kakaoApiClient.userMe(token);

        Object idObj = me.get("id");
        if (idObj == null) {
            throw new InvalidSocialTokenException();
        }

        String providerUserId = String.valueOf(idObj);
        String profileImageUrl = extractProfileImageUrl(me);

        return new SocialVerifiedUser("kakao", providerUserId, profileImageUrl);
    }

    private static Long getLong(Map<String, Object> map, String key) {
        if (map == null)
            return null;
        Object v = map.get(key);
        if (v == null)
            return null;
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> src, String key) {
        if (src == null)
            return null;
        Object v = src.get(key);
        if (v instanceof Map<?, ?> m)
            return (Map<String, Object>) m;
        return null;
    }

    private static String getString(Map<String, Object> src, String key) {
        if (src == null)
            return null;
        Object v = src.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static String extractProfileImageUrl(Map<String, Object> attributes) {
        // kakao_account.profile.profile_image_url 우선
        String url = getString(getMap(getMap(attributes, "kakao_account"), "profile"), "profile_image_url");
        if (url != null)
            return url;

        // properties.profile_image fallback
        url = getString(getMap(attributes, "properties"), "profile_image");
        if (url != null)
            return url;

        // profile_image fallback
        return getString(getMap(getMap(attributes, "kakao_account"), "profile"), "profile_image");
    }
}
