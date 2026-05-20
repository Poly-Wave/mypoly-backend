package com.polywave.notificationservice.client.dto;

/**
 * bill-service /internal/segments/bookmarked-unvoted 응답 단건.
 */
public record BookmarkedUnvotedBillDto(Long userId, Long billId, String billTitle) {
}
