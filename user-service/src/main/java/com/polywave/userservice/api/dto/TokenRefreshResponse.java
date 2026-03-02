package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access Token 재발급 응답")
public record TokenRefreshResponse(
        @Schema(description = "새로 발급된 Access Token(JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String jwt
) {
}