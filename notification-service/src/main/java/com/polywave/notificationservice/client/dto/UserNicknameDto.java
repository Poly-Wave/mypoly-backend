package com.polywave.notificationservice.client.dto;

/** user-service /internal/lookup/by-ids 응답 단건. */
public record UserNicknameDto(Long userId, String nickname) {
}
