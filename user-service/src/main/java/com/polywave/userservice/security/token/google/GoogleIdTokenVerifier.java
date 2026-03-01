package com.polywave.userservice.security.token.google;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import com.polywave.userservice.security.token.SocialTokenVerifier;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import java.net.URL;
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
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class GoogleIdTokenVerifier implements SocialTokenVerifier {

    private final boolean enabled;
    private final JwtDecoder jwtDecoder;

    public GoogleIdTokenVerifier(
            // 콤마(,) 구분으로 여러 Client ID 허용 (Android/iOS/Web 등)
            @Value("${social.google.allowed-audiences:}") String allowedAudiencesCsv,

            // (하위호환) 단일 client-id 설정이 남아있으면 같이 허용
            @Value("${social.google.client-id:}") String legacyClientId,

            @Value("${social.google.issuer:https://accounts.google.com}") String issuer,
            @Value("${social.google.jwk-set-uri:https://www.googleapis.com/oauth2/v3/certs}") String jwkSetUri) {

        Set<String> allowedAudiences = parseCsvToSet(allowedAudiencesCsv);
        if (legacyClientId != null && !legacyClientId.isBlank()) {
            allowedAudiences.add(legacyClientId.trim());
        }

        if (allowedAudiences.isEmpty()) {
            // 설정이 비어있으면 verifier 자체 비활성화 (UnsupportedSocialLoginException 흐름 유도)
            this.enabled = false;
            this.jwtDecoder = null;
            return;
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Google ID token iss는 두 가지 형태가 섞여 나올 수 있음:
        // - https://accounts.google.com
        // - accounts.google.com
        Set<String> allowedIssuers = normalizeGoogleIssuers(issuer);

        OAuth2TokenValidator<Jwt> issuerValidator = jwt -> {
            // ✅ 너희 스프링 버전에서는 getIssuer()가 URL을 리턴함
            URL tokenIssuer = jwt.getIssuer();
            String iss = tokenIssuer == null ? null : tokenIssuer.toString();

            if (iss != null && allowedIssuers.contains(iss)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Invalid issuer(iss)", null));
        };

        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            boolean ok = jwt.getAudience() != null
                    && jwt.getAudience().stream().anyMatch(allowedAudiences::contains);
            return ok
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(
                            new OAuth2Error("invalid_token", "Invalid audience(aud)", null));
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

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

    private static Set<String> normalizeGoogleIssuers(String configuredIssuer) {
        Set<String> issuers = new LinkedHashSet<>();
        if (configuredIssuer != null && !configuredIssuer.isBlank()) {
            String iss = configuredIssuer.trim();
            issuers.add(iss);

            // configuredIssuer가 https://accounts.google.com 이면 accounts.google.com 도 허용
            if (iss.startsWith("https://")) {
                issuers.add(iss.substring("https://".length()));
            } else {
                issuers.add("https://" + iss);
            }
        }

        // 안전망: Google 고정 issuer 2종은 기본 허용
        issuers.add("https://accounts.google.com");
        issuers.add("accounts.google.com");
        return issuers;
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

            // 사용자 고유 ID
            String sub = jwt.getSubject();
            String picture = jwt.getClaimAsString("picture"); // optional

            if (sub == null || sub.isBlank()) {
                throw new InvalidSocialTokenException();
            }
            return new SocialVerifiedUser("google", sub, picture);
        } catch (JwtException e) {
            throw new InvalidSocialTokenException();
        }
    }
}