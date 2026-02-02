package com.polywave.userservice.api.dto;

public record NicknameAvailabilityResponse(
        boolean available
) {
    public static NicknameAvailabilityResponse of(boolean available) {
        return new NicknameAvailabilityResponse(available);
    }
}