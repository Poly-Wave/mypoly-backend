package com.polywave.userservice.security.token.google;

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
public class GoogleIdTokenVerifier implements SocialTokenVerifier {

    private final boolean enabled;
    private final JwtDecoder jwtDecoder;

    public GoogleIdTokenVerifier(
            @Value("${social.google.client-id:}") String clientId,
            @Value("${social.google.issuer:https://accounts.google.com}") String issuer,
            @Value("${social.google.jwk-set-uri:https://www.googleapis.com/oauth2/v3/certs}") String jwkSetUri
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
        return "google";
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
            String picture = jwt.getClaimAsString("picture");
            if (sub == null || sub.isBlank()) {
                throw new InvalidSocialTokenException("구글 id_token 검증 실패(sub 누락)");
            }
            return new SocialVerifiedUser("google", sub, picture);
        } catch (JwtException e) {
            throw new InvalidSocialTokenException("구글 id_token 검증 실패: " + e.getMessage());
        }
    }
}
