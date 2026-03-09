package com.polywave.userservice.api.controller.internal;

import com.polywave.userservice.application.session.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 다른 서비스(bill-service 등)에서 현재 토큰의 세션이 유효한지 확인할 때 사용하는 내부 API.
 *
 * 주의:
 * - 이 엔드포인트 자체도 JWT 인증을 통과해야 진입 가능하다.
 * - 즉, user-service의 JwtAuthenticationFilter가 access token의 sid를 먼저 검증한다.
 * - 여기서는 최종적으로 userId / sid를 다시 한 번 명시적으로 검증하여 응답한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth/session")
public class InternalAuthSessionController {

    private final AuthSessionService authSessionService;

    @GetMapping("/validate")
    public ResponseEntity<SessionValidationResponse> validate(
            @RequestParam Long userId,
            @RequestParam String sid,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        boolean valid = authorizationHeader != null && authSessionService.isValidSession(userId, sid);
        return ResponseEntity.ok(new SessionValidationResponse(valid));
    }

    public record SessionValidationResponse(boolean valid) {
    }
}