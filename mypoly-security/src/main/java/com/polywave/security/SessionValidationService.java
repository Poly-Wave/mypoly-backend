package com.polywave.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT의 세션 식별자(sid)가 현재 서버가 인정하는 유효 세션인지 검사한다.
 *
 * 구현체는 서비스별로 다를 수 있다.
 * - user-service: DB에 저장된 현재 auth_session_id와 직접 비교
 * - other service: user-service 내부 API 호출 등으로 위임
 */
public interface SessionValidationService {
    boolean isValid(HttpServletRequest request, Long userId, String sid);
}