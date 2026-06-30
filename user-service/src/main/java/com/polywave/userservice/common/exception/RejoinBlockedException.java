package com.polywave.userservice.common.exception;

import com.polywave.common.exception.BusinessException;

/**
 * 탈퇴 후 재가입 제한 기간(7일) 내에 동일 소셜 계정으로 재가입을 시도한 경우.
 */
public class RejoinBlockedException extends BusinessException {

    public RejoinBlockedException() {
        super(UserErrorCode.REJOIN_BLOCKED);
    }
}
