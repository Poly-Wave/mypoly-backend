package com.polywave.billservice.client.dto;

public record UserProfileResponse(
        Long userId,
        String birthDate
) {
}
