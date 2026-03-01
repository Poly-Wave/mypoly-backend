package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "SDK 로그인 후 앱이 전달하는 소셜 토큰 로그인 요청", requiredProperties = {"tokenType", "token"})
public record SocialTokenLoginRequest(
        @Schema(description = "토큰 타입", example = "ACCESS_TOKEN")
        @NotNull(message = "tokenType은 필수입니다.")
        SocialTokenType tokenType,

        @Schema(description = "SDK로 획득한 토큰 문자열", example = "eyJhbGciOi... 또는 kakao access_token 값")
        @NotBlank(message = "token은 필수입니다.")
        String token
) {
}
