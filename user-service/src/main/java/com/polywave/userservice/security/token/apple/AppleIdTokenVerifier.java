package com.polywave.userservice.security.token.apple;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import com.polywave.userservice.security.token.SocialTokenVerifier;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class AppleIdTokenVerifier implements SocialTokenVerifier {

    private final boolean enabled;
    private final JwtDecoder jwtDecoder;

    public AppleIdTokenVerifier(
            // 콤마 구분 허용 목록 (DEV/PROD를 한 서버에서 같이 열고 싶을 때도 여기로 처리)
            @Value("${social.apple.allowed-audiences:}") String allowedAudiencesCsv,

            // (하위호환) 기존 단일 client-id도 같이 허용
            @Value("${social.apple.client-id:}") String legacyClientId,

            @Value("${social.apple.issuer:https://appleid.apple.com}") String issuer,
            @Value("${social.apple.jwk-set-uri:https://appleid.apple.com/auth/keys}") String jwkSetUri) {

        Set<String> allowedAudiences = parseCsvToSet(allowedAudiencesCsv);
        if (legacyClientId != null && !legacyClientId.isBlank()) {
            allowedAudiences.add(legacyClientId.trim());
        }

        if (allowedAudiences.isEmpty()) {
            this.enabled = false;
            this.jwtDecoder = null;
            return;
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            boolean ok = jwt.getAudience() != null
                    && jwt.getAudience().stream().anyMatch(allowedAudiences::contains);

            return ok
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(
                            new OAuth2Error("invalid_token", "Invalid audience(aud)", null));
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));

        this.enabled = true;
        this.jwtDecoder = decoder;
    }

    private static Set<String> parseCsvToSet(String csv) {
        if (csv == null || csv.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String provider() {
        return "apple";
    }

    @Override
    public boolean supports(SocialTokenType tokenType) {
        return enabled && tokenType == SocialTokenType.ID_TOKEN;
    }

    @Override
    public SocialVerifiedUser verify(String token, SocialTokenType tokenType) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            String sub = jwt.getSubject();
            if (sub == null || sub.isBlank()) {
                throw new InvalidSocialTokenException();
            }

            return new SocialVerifiedUser("apple", sub, null);
        } catch (JwtException e) {
            throw new InvalidSocialTokenException();
        }
    }
}