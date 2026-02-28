package com.polywave.billservice.common.exception;

import com.polywave.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BillErrorCode implements ErrorCode {

    /** 관심사/카테고리를 요청했으나 해당 사용자의 온보딩 상태가 올바르지 않은 경우 (예: 이미 온보딩 완료) */
    INVALID_ONBOARDING_STATUS(HttpStatus.BAD_REQUEST, "INVALID_ONBOARDING_STATUS"),

    /** bill-service 내부 혹은 타 서비스 통신 시 필수인 JWT 인증 토큰이 없는 경우 */
    MISSING_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "MISSING_JWT_TOKEN"),

    /** user-service 등 다른 마이크로서비스 호출 자체가 실패했거나 네트워크 타임아웃/서킷브레이커 오픈 시 발생 */
    USER_SERVICE_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER_SERVICE_API_FAILED");

    private final HttpStatus httpStatus;
    private final String code;
}
