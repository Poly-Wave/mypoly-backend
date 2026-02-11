package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 사용 가능 여부 응답")
public record NicknameAvailabilityResponse(
        @Schema(description = "사용 가능 여부", example = "true")
        boolean available
) {
    public static NicknameAvailabilityResponse of(boolean available) {
        return new NicknameAvailabilityResponse(available);
    }
}
