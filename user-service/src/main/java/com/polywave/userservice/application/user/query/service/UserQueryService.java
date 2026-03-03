package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;
import com.polywave.userservice.application.user.query.result.UserMeResult;

public interface UserQueryService {
    OnboardingStatusResult getOnboardingStatus(Long userId);

    UserMeResult getMe(Long userId);
}