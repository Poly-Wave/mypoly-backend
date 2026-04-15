package com.polywave.billservice.application.bill.query.result;

import java.time.LocalDate;

public record BillDetailResult(
        Long billId,
        String officialTitle,
        LocalDate proposalDate,
        String representativeProposerName,
        Integer proposerCount,
        String detailUrl,
        String currentProcStageCode,
        String currentProcStageName,
        Integer currentProcStageOrder,
        String currentPassGubn,
        String currentGeneralResult,
        String aiHeadline,
        String aiSummary
) {
}