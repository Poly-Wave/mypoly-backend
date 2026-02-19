package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앱에서 전달하는 소셜 토큰 타입")
public enum SocialTokenType {
    ACCESS_TOKEN,
    ID_TOKEN
}
