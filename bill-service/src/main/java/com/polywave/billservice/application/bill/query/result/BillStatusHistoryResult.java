package com.polywave.billservice.application.bill.query.result;

import java.time.LocalDate;

public record BillStatusHistoryResult(
        LocalDate procDate,
        String procStageCode,
        String procStageName,
        Integer procStageOrder,
        String passGubn,
        String generalResult
) {
}