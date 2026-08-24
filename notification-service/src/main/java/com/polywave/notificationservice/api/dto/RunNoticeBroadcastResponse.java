package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notice.scheduler.NoticeBroadcastScheduler.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 공지사항 broadcast 수동 실행 응답")
public record RunNoticeBroadcastResponse(
        @Schema(description = "이번 실행에서 broadcast 처리된 공지사항 건수", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int noticesBroadcasted,

        @Schema(description = "실제 발급된 알림함 항목 수(전체 대상 유저 합계)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
        int notificationsSent
) {
    public static RunNoticeBroadcastResponse from(Result r) {
        return new RunNoticeBroadcastResponse(r.noticesBroadcasted(), r.notificationsSent());
    }
}
