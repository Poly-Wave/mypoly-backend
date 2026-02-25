package com.polywave.common.exception;

/**
 * 공통 에러 코드 상수
 * enum으로 관리하여 타입 안정성 및 확장성 도모
 */
public enum ErrorCode {
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    BAD_REQUEST,
    INTERNAL_SERVER_ERROR,
    VALIDATION_ERROR
}
