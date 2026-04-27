package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.service.BillUiStage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "의안 진행 단계 응답")
public record BillStageResponse(
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

        @Schema(description = "원천 통과 구분", example = "원안가결", requiredMode = RequiredMode.REQUIRED)
        String passGubn,

        @Schema(description = "원천 처리 결과", example = "가결", requiredMode = RequiredMode.REQUIRED)
        String generalResult
) {
    public static BillStageResponse from(BillDetailResult detail) {
        BillUiStage mapping = BillUiStage.fromProcStageOrder(detail.currentProcStageOrder());

        return new BillStageResponse(
                nullToEmpty(detail.currentProcStageCode()),
                nullToEmpty(detail.currentProcStageName()),
                mapping.code(),
                mapping.displayName(),
                mapping.order(),
                nullToEmpty(detail.currentPassGubn()),
                nullToEmpty(detail.currentGeneralResult())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}