package com.polywave.notificationservice.common.exception;

import com.polywave.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    /** 알림 정책을 찾을 수 없는 경우 */
    NOTIFICATION_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_POLICY_NOT_FOUND"),

    /** 알림 정책 정보가 유효하지 않은 경우 */
    INVALID_NOTIFICATION_POLICY(HttpStatus.BAD_REQUEST, "INVALID_NOTIFICATION_POLICY"),

    /** 사용자 알림을 찾을 수 없는 경우 */
    USER_NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOTIFICATION_NOT_FOUND");

    private final HttpStatus httpStatus;
    private final String code;
}
