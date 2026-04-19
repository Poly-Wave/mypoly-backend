package com.polywave.billservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileResponse(
        Long userId,
        String birthDate
) {
}
