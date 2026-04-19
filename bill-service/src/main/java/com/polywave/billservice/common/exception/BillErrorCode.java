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

    /** 지원하지 않는 아젠다 탭 코드로 조회를 요청한 경우 */
    INVALID_AGENDA_TAB_CODE(HttpStatus.BAD_REQUEST, "INVALID_AGENDA_TAB_CODE"),

    /** 요청한 의안을 찾을 수 없는 경우 */
    BILL_NOT_FOUND(HttpStatus.NOT_FOUND, "BILL_NOT_FOUND"),

    /** 요청한 국회의원을 찾을 수 없는 경우 */
    BILL_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "BILL_MEMBER_NOT_FOUND"),

    /** bill-service 내부 혹은 타 서비스 통신 시 필수인 JWT 인증 토큰이 없는 경우 */
    MISSING_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "MISSING_JWT_TOKEN"),

    /** user-service 등 다른 마이크로서비스 호출 자체가 실패했거나 네트워크 타임아웃/서킷브레이커 오픈 시 발생 */
    USER_SERVICE_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER_SERVICE_API_FAILED"),

    /**
     * user-service 프로필에 생년월일이 없어 연령대 기반 기능(내 또래 탭, 투표 시 연령대 스냅샷 등)을 수행할 수 없는 경우
     */
    USER_BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "USER_BIRTH_DATE_REQUIRED"),

    /** 생년월일 값이 기대 형식(yyyyMMdd)이 아니거나 연령대 산정에 사용할 수 없는 경우 */
    INVALID_USER_BIRTH_DATE(HttpStatus.BAD_REQUEST, "INVALID_USER_BIRTH_DATE");

    private final HttpStatus httpStatus;
    private final String code;
}