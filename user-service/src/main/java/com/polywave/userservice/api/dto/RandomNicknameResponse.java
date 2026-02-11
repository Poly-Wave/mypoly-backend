package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "랜덤 닉네임 생성 응답")
public record RandomNicknameResponse(
        @Schema(description = "서버가 생성한 랜덤 닉네임", example = "파란당근도사")
        String nickname
) {
    public static RandomNicknameResponse of(String nickname) {
        return new RandomNicknameResponse(nickname);
    }
}
