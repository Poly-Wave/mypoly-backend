package com.polywave.userservice.application.nickname.command.service;

import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NicknameCommandServiceImpl implements NicknameCommandService {

    private final UserCommandRepository userCommandRepository;

    @Override
    public void assignNickname(Long userId, String nickname, NicknameAvailabilityResult availabilityResult) {

        // 1. 닉네임 검증
        if (!availabilityResult.available()) {
            throw new IllegalStateException("사용 불가능한 닉네임입니다.");
        }

        // 2. 사용자 조회
        User user = userCommandRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 닉네임 변경
        user.changeNickname(nickname);
    }
}
