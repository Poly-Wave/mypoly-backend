package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.OnBoardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "온보딩 상태 조회 응답", requiredProperties = {"onboardingStatus"})
public record OnboardingStatusResponse(
        @Schema(description = "온보딩 상태", example = "COMPLETE")
        OnBoardingStatus onboardingStatus) {
    public static OnboardingStatusResponse of(OnBoardingStatus onboardingStatus) {
        return new OnboardingStatusResponse(onboardingStatus);
    }
}
