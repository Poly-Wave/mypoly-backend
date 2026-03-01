package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.OnBoardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "온보딩 상태 업데이트 요청", requiredProperties = {"onboardingStatus"})
public record UpdateOnboardingStatusRequest(
        @NotNull(message = "온보딩 상태는 필수입니다.") @Schema(description = "온보딩 상태", example = "CATEGORY", allowableValues = {
                "SIGNUP", "ONBOARDING", "CATEGORY", "COMPLETE" }) OnBoardingStatus onboardingStatus) {
}
