package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.user.query.result.UserNicknameResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[Internal] 사용자 닉네임 lookup 응답 단건")
public record UserNicknameResponse(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,

        @Schema(description = "닉네임 (없으면 빈 문자열)", example = "당근도사", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname
) {
    public static UserNicknameResponse from(UserNicknameResult r) {
        return new UserNicknameResponse(r.userId(), r.nickname() == null ? "" : r.nickname());
    }
}
