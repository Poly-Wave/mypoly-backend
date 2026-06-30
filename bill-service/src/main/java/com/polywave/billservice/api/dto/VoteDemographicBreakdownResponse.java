package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.VoteDemographicBreakdownResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "투표 참여자 인구통계 구간별 집계")
public record VoteDemographicBreakdownResponse(
        @Schema(
                description = "구간 코드. ageBand: TEN/TWENTY/.../SIXTY_PLUS, gender: MAN/WOMAN",
                example = "TWENTY",
                requiredMode = RequiredMode.REQUIRED
        )
        String segment,

        @Schema(description = "해당 구간 투표 수", example = "45", requiredMode = RequiredMode.REQUIRED)
        long count,

        @Schema(description = "해당 구간 비율 (0~1). 분모는 해당 구분값이 있는 투표 수 합계", example = "0.23", requiredMode = RequiredMode.REQUIRED)
        double ratio
) {
    public static VoteDemographicBreakdownResponse from(VoteDemographicBreakdownResult result) {
        return new VoteDemographicBreakdownResponse(result.segment(), result.count(), result.ratio());
    }
}
