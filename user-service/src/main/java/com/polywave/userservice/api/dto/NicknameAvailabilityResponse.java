package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 사용 가능 여부 응답", requiredProperties = { "available", "status" })
public record NicknameAvailabilityResponse(
        @Schema(description = "사용 가능 여부", example = "true") boolean available,
        @Schema(description = "처리 결과 상태 (AVAILABLE, DUPLICATED, FORBIDDEN 등)", example = "AVAILABLE") NicknameAvailabilityStatus status) {
    public static NicknameAvailabilityResponse of(boolean available, NicknameAvailabilityStatus status) {
        return new NicknameAvailabilityResponse(available, status);
    }
}
