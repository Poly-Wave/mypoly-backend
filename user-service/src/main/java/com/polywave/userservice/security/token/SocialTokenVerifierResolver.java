package com.polywave.userservice.security.token;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.common.exception.UnsupportedSocialLoginException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialTokenVerifierResolver {

    private final List<SocialTokenVerifier> verifiers;

    public SocialTokenVerifier resolve(String provider, SocialTokenType tokenType) {
        return verifiers.stream()
                .filter(v -> v.provider().equalsIgnoreCase(provider))
                .filter(v -> v.supports(tokenType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedSocialLoginException(
                        "지원하지 않는 소셜 로그인 요청입니다. provider=" + provider + ", tokenType=" + tokenType
                ));
    }
}
