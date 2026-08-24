package com.polywave.userservice.common.exception;

import com.polywave.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * user-service 도메인 특화 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    /** 사용자 정보를 찾을 수 없는 경우 (주로 조회나 수정 시 발생) */
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"),

    /** 소셜 로그인 제공자 및 고유 ID로 조회 시 이미 가입된 사용자인 경우 */
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_ALREADY_EXISTS"),

    /** 닉네임 설정 시 이미 다른 사용자가 사용 중인 경우 중복 에러 */
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "DUPLICATE_NICKNAME"),

    /** 닉네임 유효성(금칙어 등) 검사에 실패한 경우 */
    FORBIDDEN_NICKNAME(HttpStatus.BAD_REQUEST, "FORBIDDEN_NICKNAME"),

    /** 요청된 약관 ID에 해당하는 약관 데이터가 DB에 없는 경우 */
    TERMS_NOT_FOUND(HttpStatus.NOT_FOUND, "TERMS_NOT_FOUND"),

    /** 소셜 프로바이더(카카오, 구글, 애플 등)의 OAuth2 엑세스 토큰이 유효하지 않은 경우 */
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_SOCIAL_TOKEN"),

    /** 서버에서 현재 지원하지 않는 소셜 로그인 제공자(provider)를 요청한 경우 */
    UNSUPPORTED_SOCIAL_LOGIN(HttpStatus.BAD_REQUEST, "UNSUPPORTED_SOCIAL_LOGIN"),

    /** 프로필 업데이트(거주지 등)를 요청했으나, 현재 회원의 온보딩 상태가 CATEGORY 단계가 아닌 경우 */
    INVALID_ONBOARDING_STATUS_FOR_PROFILE_UPDATE(HttpStatus.BAD_REQUEST,
            "INVALID_ONBOARDING_STATUS_FOR_PROFILE_UPDATE"),

    /** 요청 본문에 필수 데이터가 누락되거나 비어있는 경우 */
    EMPTY_REQUEST_BODY(HttpStatus.BAD_REQUEST, "EMPTY_REQUEST_BODY"),

    /** 사용자 식별 정보(예: userId)가 누락되어 인증할 수 없는 경우 */
    MISSING_USER_INFO(HttpStatus.UNAUTHORIZED, "MISSING_USER_INFO"),

    /** 본인의 정보가 아닌 다른 사용자의 정보를 수정하려 한 경우 */
    FORBIDDEN_NOT_OWNER(HttpStatus.FORBIDDEN, "FORBIDDEN_NOT_OWNER"),

    /** 약관 동의 리스트 자체가 요청에 없는 경우 */
    MISSING_TERMS_AGREE(HttpStatus.BAD_REQUEST, "MISSING_TERMS_AGREE"),

    /** 개별 약관 동의 항목에서 약관 ID가 누락된 경우 */
    MISSING_TERMS_ID(HttpStatus.BAD_REQUEST, "MISSING_TERMS_ID"),

    /** 클라이언트가 요청한 약관 ID 목록 중 일부가 DB에 존재하지 않는 경우 */
    INVALID_TERMS_ID(HttpStatus.BAD_REQUEST, "INVALID_TERMS_ID"),

    /** 랜덤 닉네임 생성 과정 자체에서 모종의 이유로 실패한 경우 */
    NICKNAME_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NICKNAME_GENERATION_FAILED"),

    /** 랜덤 닉네임 생성을 위한 단어 조합 데이터(명사/형용사 등)가 DB에 충분하지 않은 경우 */
    NICKNAME_DATA_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "NICKNAME_DATA_NOT_FOUND");

    private final HttpStatus httpStatus;
    private final String code;
}
