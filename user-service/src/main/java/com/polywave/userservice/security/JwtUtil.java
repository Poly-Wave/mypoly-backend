package com.polywave.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String secret;

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24시간

    public String generateToken(String userId) {
        SecretKey key = getSecretKey();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private SecretKey getSecretKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET 환경변수가 비어있습니다.");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ignored) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET 길이가 너무 짧습니다. 32bytes 이상 권장");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
