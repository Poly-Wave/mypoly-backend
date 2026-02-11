package com.polywave.billservice.api.dto;

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

        @Schema(description = "응답 메시지(성공/실패 사유). 성공 시 null일 수 있습니다.", example = "카테고리 목록 조회 성공")
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

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
