package com.polywave.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "MyPoly 공통 에러 응답 포맷")
public record ErrorResponse(
        @Schema(description = "에러 코드", example = "bad_request") String code) {
}
