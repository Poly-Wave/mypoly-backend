package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.application.user.command.UserUpdateBasicProfileCommand;
import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.common.exception.DuplicateNicknameException;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.common.exception.InvalidOnboardingStatusException;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final NicknamePolicyService nicknamePolicyService;

    @Override
    public void updateUserProfile(Long userId, UserUpdateProfileCommand command) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getOnboardingStatus() != OnBoardingStatus.CATEGORY) {
            throw new InvalidOnboardingStatusException();
        }

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
    public void updateUserBasicProfile(Long userId, UserUpdateBasicProfileCommand command) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validateBasicProfileUpdatable(user);
        updateNicknameIfNeeded(user, command.nickname());

        String combinedAddress = String.format("%s %s %s",
                command.sido(),
                command.sigungu(),
                command.emdName());

        user.updateProfile(
                command.gender(),
                command.birthDate(),
                command.sido(),
                command.sigungu(),
                command.emdName(),
                combinedAddress);
    }

    @Override
    public void updateUserOnboardingStatus(Long userId, OnBoardingStatus onBoardingStatus) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateOnBoardingStatus(onBoardingStatus);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userCommandRepository.delete(user);
    }

    private void validateBasicProfileUpdatable(User user) {
        if (user.getOnboardingStatus() != OnBoardingStatus.COMPLETE) {
            throw new InvalidOnboardingStatusException();
        }
    }

    private void updateNicknameIfNeeded(User user, String rawNickname) {
        String normalizedNickname = NicknameNormalizer.normalize(rawNickname);
        String currentNickname = NicknameNormalizer.normalize(user.getNickname());

        if (normalizedNickname.equals(currentNickname)) {
            return;
        }

        if (nicknamePolicyService.isForbidden(normalizedNickname)) {
            throw new ForbiddenNicknameException();
        }

        if (userQueryRepository.existsByNickname(normalizedNickname)) {
            throw new DuplicateNicknameException();
        }

        user.changeNickname(normalizedNickname);
    }
}