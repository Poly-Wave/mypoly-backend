package com.polywave.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 전 서비스 공통 JSON 응답 포맷
 *
 * <pre>
 * {
 *   "success": true|false,
 *   "code": "USER_NOT_FOUND"|null,
 *   "message": "...",
 *   "data": {...}|null
 * }
 * </pre>
 */
@Schema(name = "ApiResponse", description = "MyPoly 공통 API 응답 포맷")
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "에러 코드 (실패 시에만 존재). 클라이언트가 에러 분기 처리에 사용", example = "USER_NOT_FOUND", nullable = true)
        String code,

        @Schema(description = "응답 메시지(성공/실패 사유). 성공 시 null일 수 있습니다.", example = "닉네임 사용 가능 여부 조회 성공")
        String message,

        @Schema(description = "응답 데이터. 성공 시 payload, 실패 시 null", nullable = true)
        T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, null, message, data);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, null, message, null);
    }

    /**
     * 실패 응답 (에러 코드 없음)
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message, null);
    }

    /**
     * 실패 응답 (에러 코드 포함)
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
