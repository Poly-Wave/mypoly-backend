package com.polywave.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SessionValidationService sessionValidationService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, SessionValidationService sessionValidationService) {
        this.jwtUtil = jwtUtil;
        this.sessionValidationService = sessionValidationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        // 토큰 없으면 그냥 통과 (최종 401/permitAll은 SecurityConfig가 처리)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 있는데 검증 실패면 인증 세팅 안 함 -> EntryPoint에서 401 처리
        if (jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.extractUserId(token);
            String sessionId = jwtUtil.extractSessionId(token);

            // access token의 sid와 현재 유효 세션을 함께 비교해야
            // 다른 기기 로그인 시 기존 기기의 access token도 즉시 무효화할 수 있다.
            if (sessionId != null && sessionValidationService.isValid(request, userId, sessionId)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null || !bearer.startsWith("Bearer ")) return null;
        String token = bearer.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}