package com.polywave.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.userservice.api.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // SecurityConfig permitAll과 동일한 기준으로 whitelist 처리
        return SecurityEndpoints.PUBLIC_REQUEST_MATCHER.matches(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveBearerToken(request);

        // 토큰이 없으면 그냥 통과 -> 이후 Security가 401 처리(EntryPoint)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String userId = jwtUtil.getUserIdFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            reject(response, "토큰이 만료되었습니다.");
        } catch (SecurityException e) {
            reject(response, "토큰 서명이 올바르지 않습니다.");
        } catch (MalformedJwtException e) {
            reject(response, "토큰 형식이 올바르지 않습니다.");
        } catch (UnsupportedJwtException e) {
            reject(response, "지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            reject(response, "토큰이 비어있거나 올바르지 않습니다.");
        } catch (JwtException e) {
            reject(response, "유효하지 않은 토큰입니다.");
        }
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();

        if (response.isCommitted()) return;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(message));
    }

    private static String resolveBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
