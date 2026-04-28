package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "의안 카테고리 응답")
public record BillCategorySummaryResponse(
        @Schema(description = "카테고리 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
        Long categoryId,

        @Schema(description = "카테고리 코드", example = "ECONOMY", requiredMode = RequiredMode.REQUIRED)
        String categoryCode,

        @Schema(description = "카테고리명", example = "경제", requiredMode = RequiredMode.REQUIRED)
        String categoryName,

        @Schema(description = "카테고리 순위", example = "1", requiredMode = RequiredMode.REQUIRED)
        Integer rankOrder
) {
    public static BillCategorySummaryResponse from(BillCategoryResult result) {
        if (result == null) {
            return empty();
        }

        return new BillCategorySummaryResponse(
                result.categoryId() == null ? 0L : result.categoryId(),
                nullToEmpty(result.categoryCode()),
                nullToEmpty(result.categoryName()),
                result.rankOrder() == null ? 0 : result.rankOrder()
        );
    }

    public static BillCategorySummaryResponse empty() {
        return new BillCategorySummaryResponse(0L, "", "", 0);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}