package com.polywave.userservice.domain;

/**
 * 사용자 계정 상태.
 * WITHDRAWN 은 탈퇴(soft-delete) 상태로, PII 가 말소되고 7일 재가입 차단 판정 대상이 된다.
 * 같은 uid 재활성을 위해 행 자체는 보존된다.
 */
public enum UserStatus {
    ACTIVE,
    WITHDRAWN
}
