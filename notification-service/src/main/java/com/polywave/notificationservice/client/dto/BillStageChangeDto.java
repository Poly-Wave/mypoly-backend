package com.polywave.notificationservice.client.dto;

/** bill-service /internal/segments/bookmarked-stage-changes 응답 단건. */
public record BillStageChangeDto(
        Long userId,
        Long billId,
        String billTitle,
        String fromStageCode,
        String fromStageName,
        String toStageCode,
        String toStageName
) {
}
