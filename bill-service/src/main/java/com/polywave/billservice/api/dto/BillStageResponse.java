package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의안 진행 단계 응답")
public record BillStageResponse(
        @Schema(description = "원천 단계 코드", example = "PROC_EXAM")
        String rawStageCode,

        @Schema(description = "원천 단계명", example = "소관위심사")
        String rawStageName,

        @Schema(description = "앱용 단계 코드", example = "REVIEW")
        String uiStepCode,

        @Schema(description = "앱용 단계명", example = "심사")
        String uiStepName,

        @Schema(description = "앱용 단계 순서", example = "2")
        Integer uiStepOrder,

        @Schema(description = "원천 통과 구분", example = "원안가결")
        String passGubn,

        @Schema(description = "원천 처리 결과", example = "가결")
        String generalResult
) {
    public static BillStageResponse from(BillDetailResult detail) {
        StageUiMapping mapping = StageUiMapping.from(detail.currentProcStageOrder());

        return new BillStageResponse(
                detail.currentProcStageCode(),
                detail.currentProcStageName(),
                mapping.code(),
                mapping.name(),
                mapping.order(),
                detail.currentPassGubn(),
                detail.currentGeneralResult()
        );
    }

    private record StageUiMapping(String code, String name, Integer order) {
        private static StageUiMapping from(Integer currentProcStageOrder) {
            if (currentProcStageOrder == null || currentProcStageOrder <= 1) {
                return new StageUiMapping("RECEIVED", "접수", 1);
            }
            if (currentProcStageOrder == 2) {
                return new StageUiMapping("REVIEW", "심사", 2);
            }
            if (currentProcStageOrder == 3) {
                return new StageUiMapping("DECISION", "의결", 3);
            }
            return new StageUiMapping("COMPLETED", "완료", 4);
        }
    }
}