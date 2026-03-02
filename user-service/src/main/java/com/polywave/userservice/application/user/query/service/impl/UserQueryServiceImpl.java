package com.polywave.userservice.application.user.query.service.impl;

import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;
import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.application.user.query.service.UserQueryService;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public OnboardingStatusResult getOnboardingStatus(Long userId) {
        OnBoardingStatus status = userQueryRepository.findOnboardingStatusByUserId(userId);
        if (status == null) {
            throw new UserNotFoundException();
        }
        return new OnboardingStatusResult(status);
    }

    @Override
    public UserMeResult getMe(Long userId) {
        UserMeResult result = userQueryRepository.findUserMeByUserId(userId);
        if (result == null) {
            throw new UserNotFoundException();
        }
        return result;
    }
}