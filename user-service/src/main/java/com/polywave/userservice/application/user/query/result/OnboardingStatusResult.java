package com.polywave.userservice.application.user.query.result;

import com.polywave.userservice.domain.OnBoardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "온보딩 상태 조회 결과", requiredProperties = {"status"})
public record OnboardingStatusResult(
        @Schema(description = "온보딩 상태", example = "COMPLETE")
        OnBoardingStatus status) {
}
