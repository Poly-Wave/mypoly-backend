package com.polywave.billservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusResponse {
    private String onboardingStatus;

    public static OnboardingStatusResponse of(String onboardingStatus) {
        return new OnboardingStatusResponse(onboardingStatus);
    }
}
