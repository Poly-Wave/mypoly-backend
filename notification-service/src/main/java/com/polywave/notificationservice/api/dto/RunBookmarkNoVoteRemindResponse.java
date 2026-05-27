package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.scheduler.BookmarkNoVoteReminderScheduler.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 북마크 D+1 미투표 스케줄러 수동 실행 응답")
public record RunBookmarkNoVoteRemindResponse(
        @Schema(description = "행 7 발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int sent
) {
    public static RunBookmarkNoVoteRemindResponse from(Result r) {
        return new RunBookmarkNoVoteRemindResponse(r.sent());
    }
}
