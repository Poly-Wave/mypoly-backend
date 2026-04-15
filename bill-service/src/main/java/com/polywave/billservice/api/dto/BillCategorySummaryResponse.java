package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의안 카테고리 응답")
public record BillCategorySummaryResponse(
        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "카테고리 코드", example = "ECONOMY")
        String categoryCode,

        @Schema(description = "카테고리명", example = "경제")
        String categoryName,

        @Schema(description = "카테고리 순위", example = "1")
        Integer rankOrder
) {
    public static BillCategorySummaryResponse from(BillCategoryResult result) {
        return new BillCategorySummaryResponse(
                result.categoryId(),
                result.categoryCode(),
                result.categoryName(),
                result.rankOrder()
        );
    }
}