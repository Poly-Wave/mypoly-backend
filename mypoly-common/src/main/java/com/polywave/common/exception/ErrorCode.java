package com.polywave.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 프로젝트 내 모든 커스텀 예외에서 공통으로 사용할 에러 코드 규격
 */
public interface ErrorCode {
    HttpStatus getHttpStatus();

    String getCode();
}
