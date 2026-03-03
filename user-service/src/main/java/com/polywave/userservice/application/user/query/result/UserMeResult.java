package com.polywave.userservice.application.user.query.result;

import com.polywave.userservice.domain.Gender;
import com.polywave.userservice.domain.OnBoardingStatus;

public record UserMeResult(
        Long userId,
        String provider,
        String providerUserId,
        String nickname,
        OnBoardingStatus onboardingStatus,
        Gender gender,
        String birthDate,
        String profileImageUrl,
        String sido,
        String sigungu,
        String emdName,
        String address
) {
}