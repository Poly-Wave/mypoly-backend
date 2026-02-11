package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.api.dto.UserUpdateProfileRequest;
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
    public void updateUserProfile(Long userId, UserUpdateProfileRequest request) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String combinedAddress = String.format("%s %s %s",
                request.sido(),
                request.sigungu(),
                request.emdName());

        user.updateProfile(
                request.gender(),
                request.birthDate(),
                request.sido(),
                request.sigungu(),
                request.emdName(),
                combinedAddress);
    }
}
