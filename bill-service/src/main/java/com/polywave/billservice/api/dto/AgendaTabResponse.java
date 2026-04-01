package com.polywave.billservice.api.dto;

import com.polywave.billservice.domain.agenda.AgendaTab;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "안건 탭 메타 정보")
public record AgendaTabResponse(
        @Schema(description = "탭 코드", example = "HOT_DEBATE")
        String code,

        @Schema(description = "탭 라벨", example = "쟁쟁한")
        String label,

        @Schema(description = "탭 설명", example = "찬반이 팽팽한 의안")
        String description,

        @Schema(description = "표시 순서", example = "1")
        int displayOrder
) {
    public static AgendaTabResponse from(AgendaTab tab) {
        return new AgendaTabResponse(
                tab.getCode(),
                tab.getLabel(),
                tab.getDescription(),
                tab.getDisplayOrder()
        );
    }
}
