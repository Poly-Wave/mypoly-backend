package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.domain.OnBoardingStatus;

public interface UserCommandService {
    void updateUserProfile(Long userId, UserUpdateProfileCommand command);

    void updateUserOnboardingStatus(Long userId, OnBoardingStatus onBoardingStatus);

    void deleteUser(Long userId);
}