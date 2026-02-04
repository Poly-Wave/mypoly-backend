package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.category.query.result.CategoryResult;

public record CategoryResponse(
        Long id,
        String code,
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
