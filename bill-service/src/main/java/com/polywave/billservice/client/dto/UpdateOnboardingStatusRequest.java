package com.polywave.billservice.client.dto;

/**
 * user-service 내부 API 호출용 DTO.
 * OnBoardingStatus enum 값은 user-service와 동일한 문자열(SIGNUP, ONBOARDING, CATEGORY,
 * COMPLETE) 사용.
 */
public record UpdateOnboardingStatusRequest(String onboardingStatus) {

    public static UpdateOnboardingStatusRequest of(String status) {
        return new UpdateOnboardingStatusRequest(status);
    }
}
