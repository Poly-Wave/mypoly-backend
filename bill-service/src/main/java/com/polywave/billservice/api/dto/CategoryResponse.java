package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의안 카테고리 응답")
public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1")
        Long id,

        @Schema(description = "카테고리 코드(고유)", example = "DIGITAL")
        String code,

        @Schema(description = "카테고리 표시명", example = "디지털")
        String name,

        @Schema(description = "표시 순서", example = "1")
        Integer displayOrder,

        @Schema(description = "아이콘 URL", example = "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.png")
        String iconUrl
) {
    public static CategoryResponse from(CategoryResult dto, String iconUrl) {
        return new CategoryResponse(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.displayOrder(),
                iconUrl
        );
    }
}