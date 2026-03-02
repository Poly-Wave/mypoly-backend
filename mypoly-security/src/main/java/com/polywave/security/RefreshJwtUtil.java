package com.polywave.security;

import com.polywave.common.exception.CommonErrorCode;
import com.polywave.common.exception.JwtSecretConfigurationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Refresh Token 전용 JWT 유틸.
 *
 * - Access(JWT_SECRET)와 다른 secret(REFRESH_JWT_SECRET)을 사용해야 합니다.
 * - typ=refresh 클레임을 넣고, 검증 시 typ도 함께 확인합니다.
 */
@Component
public class RefreshJwtUtil {

    private final Key key;
    private final long expirationMillis;

    public RefreshJwtUtil(
            @Value("${REFRESH_JWT_SECRET}") String secret,
            // default: 30일
            @Value("${REFRESH_JWT_EXPIRATION_MILLIS:2592000000}") long expirationMillis
    ) {
        byte[] keyBytes = decodeSecret(secret);

        if (keyBytes.length < 32) {
            throw new JwtSecretConfigurationException(CommonErrorCode.JWT_SECRET_KEY_TOO_SHORT);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();

            Object typ = claims.get("typ");
            return "refresh".equals(typ);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    private static byte[] decodeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new JwtSecretConfigurationException(CommonErrorCode.JWT_SECRET_KEY_MISSING);
        }

        // JwtUtil과 동일한 정책: base64 decode 시도 -> 실패하면 raw bytes
        try {
            return Decoders.BASE64.decode(secret);
        } catch (Exception ignored) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}