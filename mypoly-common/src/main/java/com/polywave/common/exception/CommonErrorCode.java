package com.polywave.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 프레임워크나 외부 통신 등 여러 도메인에서 공통적으로 쓰이는 HTTP 에러 코드 메타 관리
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    /** 인증되지 않은 사용자가 자원에 접근하려 할 때 (주로 시큐리티 필터 단에서 처리) */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),

    /** 인증은 되었으나 특정 리소스에 권한이 없는 경우 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),

    /** 페이지, API 주소 또는 단일 리소스를 서버에서 찾을 수 없는 범용 에러 */
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),

    /** 클라이언트 파라미터나 요청 형식이 잘못된 범용 요청 에러 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),

    /** 예외 처리가 되지 않은 시스템 상의 런타임 에러나 내부 통신 장애 등 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),

    /** JWT 설정 값이 아예 존재하지 않는 서버 에러 */
    JWT_SECRET_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_SECRET_KEY_MISSING"),

    /** JWT 설정 키가 HS256 알고리즘 권장 크기(32바이트) 미만일 때의 에러 */
    JWT_SECRET_KEY_TOO_SHORT(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_SECRET_KEY_TOO_SHORT"),

    /** Bean Validation(@Valid) 등에 의해 DTO 및 필드 제약 조건이 위반된 경우 */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");

    private final HttpStatus httpStatus;
    private final String code;
}
