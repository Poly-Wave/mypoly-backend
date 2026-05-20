package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.scheduler.BillStageChangeNotifier.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[DEV] 북마크 단계 변경 알림 polling 수동 실행 응답")
public record RunBillStageChangeResponse(
        @Schema(description = "발급 건수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int sent
) {
    public static RunBillStageChangeResponse from(Result r) {
        return new RunBillStageChangeResponse(r.sent());
    }
}
