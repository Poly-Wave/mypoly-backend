package com.polywave.billservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * /internal/segments/** 경로에 대한 서비스 간 internal API 키 가드.
 *
 * - X-Internal-Api-Key 헤더가 설정값(bill-service.internal-api.key)과 일치할 때만 통과.
 * - 키가 설정되지 않은 환경에서는 fail-closed: 무조건 403.
 * - 사용자 JWT 와는 별도 채널. notification-service 스케줄러처럼 사용자 컨텍스트가 없는 호출 전용.
 */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    static final String HEADER_NAME = "X-Internal-Api-Key";
    static final String PROTECTED_PATTERN = "/internal/segments/**";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final String configuredKey;

    public InternalApiKeyFilter(@Value("${bill-service.internal-api.key:}") String configuredKey) {
        this.configuredKey = configuredKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // getServletPath() 는 context-path 가 stripped 된 경로를 반환한다.
        // (getRequestURI() 는 context-path 를 포함하므로 PROTECTED_PATTERN 과 매칭되지 않아 가드가 무력화된다)
        if (!pathMatcher.match(PROTECTED_PATTERN, request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (configuredKey == null || configuredKey.isBlank()) {
            writeForbidden(response, "INTERNAL_API_KEY_NOT_CONFIGURED");
            return;
        }

        String headerKey = request.getHeader(HEADER_NAME);
        if (headerKey == null || !configuredKey.equals(headerKey)) {
            writeForbidden(response, "FORBIDDEN");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeForbidden(HttpServletResponse response, String code) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":\"" + code + "\"}");
    }
}
