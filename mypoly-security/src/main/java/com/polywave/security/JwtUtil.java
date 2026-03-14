package com.polywave.security;

import com.polywave.common.exception.JwtSecretConfigurationException;
import com.polywave.common.exception.CommonErrorCode;
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

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${JWT_SECRET}") String secret,
            // 기존 user-service가 24시간이었으니 default도 24h로 맞춤
            @Value("${JWT_EXPIRATION_MILLIS:86400000}") long expirationMillis) {
        byte[] keyBytes = decodeSecret(secret);

        if (keyBytes.length < 32) {
            throw new JwtSecretConfigurationException(CommonErrorCode.JWT_SECRET_KEY_TOO_SHORT);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    public String createToken(Long userId, String sessionId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                // 단일 기기 로그인 정책을 위해 access token에도 현재 세션 식별자(sid)를 포함
                .claim("sid", sessionId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
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

    public String extractSessionId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object sid = claims.get("sid");
        return sid == null ? null : sid.toString();
    }

    private static byte[] decodeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new JwtSecretConfigurationException(CommonErrorCode.JWT_SECRET_KEY_MISSING);
        }

        // 기존 user-service와 호환: base64 decode 시도 -> 실패하면 raw bytes
        try {
            return Decoders.BASE64.decode(secret);
        } catch (Exception ignored) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}