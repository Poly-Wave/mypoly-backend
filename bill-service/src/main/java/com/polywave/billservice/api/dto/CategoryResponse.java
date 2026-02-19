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
        String name
) {
    public static CategoryResponse from(CategoryResult dto) {
        return new CategoryResponse(
                dto.id(),
                dto.code(),
                dto.name()
        );
    }
}
