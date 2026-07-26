package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.CoProposerResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공동발의자 정보")
public record CoProposerResponse(
        @Schema(description = "국회의원 ID. 매칭되지 않은 경우 null", example = "12")
        Long memberId,

        @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "정당. 매칭되지 않은 경우 null", example = "더불어민주당")
        String partyName,

        @Schema(description = "프로필 사진 URL. 매칭되지 않은 경우 null")
        String photoUrl
) {
    public static CoProposerResponse from(CoProposerResult result) {
        return new CoProposerResponse(
                result.memberId(),
                result.name(),
                result.partyName(),
                result.photoUrl()
        );
    }
}
