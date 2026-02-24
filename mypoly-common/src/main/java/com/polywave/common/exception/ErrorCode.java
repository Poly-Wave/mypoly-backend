package com.polywave.common.exception;

/**
 * 공통 에러 코드 상수
 * 서비스별로 확장 가능하도록 인터페이스로 정의
 */
public interface ErrorCode {
    // 공통 에러 코드
    String UNAUTHORIZED = "UNAUTHORIZED";
    String FORBIDDEN = "FORBIDDEN";
    String NOT_FOUND = "NOT_FOUND";
    String BAD_REQUEST = "BAD_REQUEST";
    String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    String VALIDATION_ERROR = "VALIDATION_ERROR";
}
