package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.notification.query.result.BillStageChangeResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[Internal] 북마크된 의안의 단계 전이 한 건")
public record BillStageChangeResponse(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,

        @Schema(description = "의안 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "의안 제목", requiredMode = Schema.RequiredMode.REQUIRED)
        String billTitle,

        @Schema(description = "이전 단계 코드(최초 단계면 null)")
        String fromStageCode,

        @Schema(description = "이전 단계명(최초 단계면 null)")
        String fromStageName,

        @Schema(description = "현재 단계 코드", requiredMode = Schema.RequiredMode.REQUIRED)
        String toStageCode,

        @Schema(description = "현재 단계명", requiredMode = Schema.RequiredMode.REQUIRED)
        String toStageName
) {
    public static BillStageChangeResponse from(BillStageChangeResult r) {
        return new BillStageChangeResponse(
                r.userId(), r.billId(), r.billTitle(),
                r.fromStageCode(), r.fromStageName(),
                r.toStageCode(), r.toStageName()
        );
    }
}
