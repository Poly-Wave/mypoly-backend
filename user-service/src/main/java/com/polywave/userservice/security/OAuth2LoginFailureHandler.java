package com.polywave.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.userservice.api.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String message = resolveUserMessage(exception);

        // 운영/디버깅용 로그 (외부 응답에는 과한 정보 노출 X)
        log.warn("OAuth2 login failure. uri={}, msg={}, ex={}",
                request.getRequestURI(),
                message,
                exception.getClass().getSimpleName());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(message));
    }

    private String resolveUserMessage(AuthenticationException exception) {
        // 1) OAuth2AuthenticationException이면 errorCode 기반 분기
        if (exception instanceof OAuth2AuthenticationException oauth2Ex && oauth2Ex.getError() != null) {
            String code = oauth2Ex.getError().getErrorCode();

            if (code == null) return "소셜 로그인 실패";

            return switch (code) {
                case "authorization_request_not_found" ->
                        "이미 처리된 로그인 요청입니다. 다시 로그인해 주세요.";
                case "invalid_state_parameter" ->
                        "로그인 세션이 만료되었거나 잘못된 요청입니다. 다시 로그인해 주세요.";
                case "access_denied" ->
                        "로그인이 취소되었습니다.";
                default -> "소셜 로그인 실패";
            };
        }

        Throwable cause = exception.getCause();
        if (cause != null && cause.getMessage() != null) {
            String causeMsg = cause.getMessage();
            if (causeMsg.contains("invalid_grant")) {
                return "인증 코드가 만료되었거나 이미 사용되었습니다. 다시 로그인해 주세요.";
            }
        }

        return "소셜 로그인 실패";
    }
}
