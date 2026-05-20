package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.notification.query.result.BookmarkedUnvotedBillResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[Internal] 북마크 D+1 미투표 (user, bill) 쌍")
public record BookmarkedUnvotedBillResponse(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,

        @Schema(description = "의안 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
        Long billId,

        @Schema(description = "의안 제목", example = "소득세법 일부개정법률안", requiredMode = Schema.RequiredMode.REQUIRED)
        String billTitle
) {
    public static BookmarkedUnvotedBillResponse from(BookmarkedUnvotedBillResult r) {
        return new BookmarkedUnvotedBillResponse(
                r.userId(),
                r.billId(),
                r.billTitle() == null ? "" : r.billTitle()
        );
    }
}
