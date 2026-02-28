package com.polywave.userservice.repository.query;

import com.polywave.userservice.domain.OnBoardingStatus;

public interface UserQueryRepository {

    boolean existsByNickname(String nickname);

    OnBoardingStatus findOnboardingStatusByUserId(Long userId);
}
