package com.polywave.userservice.security.token.apple;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import com.polywave.userservice.security.token.SocialTokenVerifier;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
public class AppleIdTokenVerifier implements SocialTokenVerifier {

    private final boolean enabled;
    private final JwtDecoder jwtDecoder;

    public AppleIdTokenVerifier(
            @Value("${social.apple.client-id:}") String clientId,
            @Value("${social.apple.issuer:https://appleid.apple.com}") String issuer,
            @Value("${social.apple.jwk-set-uri:https://appleid.apple.com/auth/keys}") String jwkSetUri
    ) {
        if (clientId == null || clientId.isBlank()) {
            this.enabled = false;
            this.jwtDecoder = null;
            return;
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(clientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience(aud)", null));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));

        this.enabled = true;
        this.jwtDecoder = decoder;
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
                throw new InvalidSocialTokenException("애플 id_token 검증 실패(sub 누락)");
            }
            return new SocialVerifiedUser("apple", sub, null);
        } catch (JwtException e) {
            throw new InvalidSocialTokenException("애플 id_token 검증 실패: " + e.getMessage());
        }
    }
}
