package com.polywave.userservice.security;

import com.polywave.security.SessionValidationService;
import com.polywave.userservice.application.session.AuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * user-service는 현재 유효 세션의 원본 데이터를 직접 가지고 있으므로
 * DB에 저장된 auth_session_id와 token sid를 바로 비교한다.
 */
@Component
@RequiredArgsConstructor
public class UserSessionValidationService implements SessionValidationService {

    private final AuthSessionService authSessionService;

    @Override
    public boolean isValid(HttpServletRequest request, Long userId, String sid) {
        return authSessionService.isValidSession(userId, sid);
    }
}