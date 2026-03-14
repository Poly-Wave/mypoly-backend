package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.domain.OnBoardingStatus;

public interface UserQueryRepository {

    boolean existsByNickname(String nickname);

    OnBoardingStatus findOnboardingStatusByUserId(Long userId);

    UserMeResult findUserMeByUserId(Long userId);

    boolean existsValidSession(Long userId, String sessionId);
}