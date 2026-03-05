package com.polywave.userservice.application.nickname.query.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 사용 가능 상태 코드")
public enum NicknameAvailabilityStatus {
    @Schema(description = "사용 가능")
    AVAILABLE,

    @Schema(description = "이미 사용 중인 닉네임")
    DUPLICATED,

    @Schema(description = "금칙어가 포함된 닉네임")
    FORBIDDEN
}
