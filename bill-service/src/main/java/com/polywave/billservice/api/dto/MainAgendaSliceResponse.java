package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "안건 메인 목록 페이지 응답")
public record MainAgendaSliceResponse(
        @Schema(description = "안건 메인 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MainAgendaResponse> content,

        @Schema(description = "현재 페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int page,

        @Schema(description = "페이지 크기", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext
) {
}
