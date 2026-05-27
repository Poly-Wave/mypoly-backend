package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.notification.query.result.UserInterestAgendaCountResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[Internal] 사용자의 관심 카테고리 매칭 신규 안건 개수")
public record UserInterestAgendaCountResponse(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,

        @Schema(description = "[start, end) 범위에 first_collected_at 이 들어간 관심 매칭 안건 개수", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
        long count
) {
    public static UserInterestAgendaCountResponse from(UserInterestAgendaCountResult r) {
        return new UserInterestAgendaCountResponse(r.userId(), r.count());
    }
}
