package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh Token으로 Access Token 재발급 요청")
public record TokenRefreshRequest(
        @Schema(description = "Refresh Token(JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @NotBlank
        String refreshToken
) {
}