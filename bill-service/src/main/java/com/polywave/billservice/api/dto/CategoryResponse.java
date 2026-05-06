package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의안 카테고리 응답")
public record CategoryResponse(
        @Schema(description = "카테고리 코드(고유)", example = "DIGITAL", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @Schema(description = "카테고리 표시명", example = "디지털", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "표시 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer displayOrder,

        @Schema(description = "아이콘 URL", example = "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.webp", requiredMode = Schema.RequiredMode.REQUIRED)
        String iconUrl,

        @Schema(description = "카테고리 배경색 HEX, # 제외", example = "46D9E3", requiredMode = Schema.RequiredMode.REQUIRED)
        String backgroundColor
) {
    public static CategoryResponse from(CategoryResult dto, String iconUrl) {
        return new CategoryResponse(
                nullToEmpty(dto.code()),
                nullToEmpty(dto.name()),
                dto.displayOrder() == null ? 0 : dto.displayOrder(),
                nullToEmpty(iconUrl),
                nullToEmpty(dto.backgroundColor())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}