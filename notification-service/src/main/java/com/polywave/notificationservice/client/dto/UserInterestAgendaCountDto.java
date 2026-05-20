package com.polywave.notificationservice.client.dto;

/** bill-service /internal/segments/interest-matched-counts 응답 단건. */
public record UserInterestAgendaCountDto(Long userId, long count) {
}
