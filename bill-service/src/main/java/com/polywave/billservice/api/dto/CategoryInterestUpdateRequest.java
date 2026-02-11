package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "사용자 관심 카테고리 저장 요청")
public record CategoryInterestUpdateRequest(
        @Schema(
                description = "관심 카테고리 ID 목록. (최소 1개)\n\n- 중복 값은 자동으로 제거됩니다.\n- 존재하지 않거나 비활성화된 카테고리 ID는 저장 시 무시됩니다.",
                example = "[1, 2, 3]"
        )
        @NotEmpty
        List<Long> categoryIds
) {
}
