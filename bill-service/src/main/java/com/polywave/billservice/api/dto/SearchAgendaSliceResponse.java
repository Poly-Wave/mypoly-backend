package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "의안 검색 목록 페이지 응답")
public record SearchAgendaSliceResponse(
        @Schema(description = "검색 결과 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<SearchAgendaResponse> content,

        @Schema(description = "현재 페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int page,

        @Schema(description = "페이지 크기", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext
) {
}
