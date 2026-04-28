package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillStatusHistoryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDate;

@Schema(description = "의안 상태 이력 응답")
public record BillStatusHistoryResponse(
        @Schema(description = "처리일", example = "2025-12-31", requiredMode = RequiredMode.REQUIRED)
        LocalDate procDate,

        @Schema(description = "원천 단계 코드", example = "PROC_EXAM", requiredMode = RequiredMode.REQUIRED)
        String rawStageCode,

        @Schema(description = "원천 단계명", example = "소관위심사", requiredMode = RequiredMode.REQUIRED)
        String rawStageName,

        @Schema(description = "앱용 단계 코드", example = "REVIEW", requiredMode = RequiredMode.REQUIRED)
        String uiStepCode,

        @Schema(description = "앱용 단계명", example = "심사", requiredMode = RequiredMode.REQUIRED)
        String uiStepName,

        @Schema(description = "앱용 단계 순서", example = "2", requiredMode = RequiredMode.REQUIRED)
        Integer uiStepOrder,

        @Schema(description = "통과 구분", example = "원안가결", requiredMode = RequiredMode.REQUIRED)
        String passGubn,

        @Schema(description = "처리 결과", example = "가결", requiredMode = RequiredMode.REQUIRED)
        String generalResult
) {
    public static BillStatusHistoryResponse from(BillStatusHistoryResult result) {
        StageUiMapping mapping = StageUiMapping.from(result.procStageOrder());

        return new BillStatusHistoryResponse(
                result.procDate(),
                nullToEmpty(result.procStageCode()),
                nullToEmpty(result.procStageName()),
                mapping.code(),
                mapping.name(),
                mapping.order(),
                nullToEmpty(result.passGubn()),
                nullToEmpty(result.generalResult())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record StageUiMapping(String code, String name, Integer order) {
        private static StageUiMapping from(Integer procStageOrder) {
            if (procStageOrder == null || procStageOrder <= 1) {
                return new StageUiMapping("RECEIVED", "접수", 1);
            }
            if (procStageOrder == 2) {
                return new StageUiMapping("REVIEW", "심사", 2);
            }
            if (procStageOrder == 3) {
                return new StageUiMapping("DECISION", "의결", 3);
            }
            return new StageUiMapping("COMPLETED", "완료", 4);
        }
    }
}