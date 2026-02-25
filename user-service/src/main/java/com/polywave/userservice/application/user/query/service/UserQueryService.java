package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;

public interface UserQueryService {
    OnboardingStatusResult getOnboardingStatus(Long userId);
}
