package com.polywave.userservice.application.session;

/**
 * 단일 기기 로그인 정책을 위한 세션 식별자(sid) 관리용 서비스.
 *
 * 현재 정책:
 * - 로그인 시 새 sid 발급
 * - refresh 시 sid는 회전하지 않고 유지
 * - 다른 기기에서 로그인하면 기존 sid는 더 이상 유효하지 않음
 */
public interface AuthSessionService {
    String issueNewSession(Long userId);
    boolean isValidSession(Long userId, String sessionId);
}