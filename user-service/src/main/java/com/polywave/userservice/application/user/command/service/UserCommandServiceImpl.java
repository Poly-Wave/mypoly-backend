package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

        private final UserCommandRepository userCommandRepository;

        @Override
        public void updateUserProfile(Long userId, UserUpdateProfileCommand command) {
                User user = userCommandRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                String combinedAddress = String.format("%s %s %s",
                                command.sido(),
                                command.sigungu(),
                                command.emdName());

                user.updateProfile(
                                command.gender(),
                                command.birthdate(),
                                command.sido(),
                                command.sigungu(),
                                command.emdName(),
                                combinedAddress);

                updateUserOnboardingStatus(userId, OnBoardingStatus.COMPLETE);
        }

        @Override
        public void updateUserOnboardingStatus(Long userId, OnBoardingStatus onBoardingStatus) {
                User user = userCommandRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                user.updateOnBoardingStatus(onBoardingStatus);
        }
}
