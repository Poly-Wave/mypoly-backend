package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    @Override
    public void createNickname(Long userId, String nickname) {

        // 1. 닉네임 정책 검증 (중복, 금칙어 등)

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 이미 닉네임이 있는 경우 방어
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalStateException("이미 닉네임이 존재합니다.");
        }

        // 4. 닉네임 생성 및 변경
        user.changeNickname(nickname);

        // 5. 저장 (JPA dirty checking)
        userRepository.save(user);
    }

    /*
     * TODO 랜덤 닉네임 생성
     *
     * 1. 랜덤 닉네임 생성
     * 2. nicknamePolicyService.validate(randomNickname)
     * 3. 중복이면 재시도 (loop or retry limit)
     * 4. user.createNickname(randomNickname)
     */

}