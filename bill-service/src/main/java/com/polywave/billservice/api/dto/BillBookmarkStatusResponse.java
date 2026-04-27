package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "의안 보관 상태 응답")
public record BillBookmarkStatusResponse(
        @Schema(description = "의안 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "현재 사용자의 보관 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
        boolean bookmarked
) {
    public static BillBookmarkStatusResponse of(Long billId, boolean bookmarked) {
        return new BillBookmarkStatusResponse(billId, bookmarked);
    }
}