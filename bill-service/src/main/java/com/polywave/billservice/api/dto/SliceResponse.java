package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "목록 페이지 응답")
public record SliceResponse<T>(
        @Schema(description = "목록 데이터")
        List<T> content,

        @Schema(description = "현재 페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "20")
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false")
        boolean hasNext
) {
    public static <T> SliceResponse<T> of(List<T> content, int page, int size, boolean hasNext) {
        return new SliceResponse<>(
                content == null ? List.of() : content,
                page,
                size,
                hasNext
        );
    }
}