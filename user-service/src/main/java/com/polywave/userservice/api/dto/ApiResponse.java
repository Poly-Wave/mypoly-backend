package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 전 서비스 공통 JSON 응답 포맷
 *
 * <pre>
 * {
 *   "success": true|false,
 *   "message": "...",
 *   "data": {...}|null
 * }
 * </pre>
 */
@Schema(name = "ApiResponse", description = "MyPoly 공통 API 응답 포맷")
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "응답 메시지(성공/실패 사유). 성공 시 null일 수 있습니다.", example = "닉네임 사용 가능 여부 조회 성공")
        String message,

        @Schema(description = "응답 데이터. 성공 시 payload, 실패 시 null", nullable = true)
        T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // 핵심: fail도 제네릭으로 (컨텍스트 타입에 맞게 추론됨)
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
