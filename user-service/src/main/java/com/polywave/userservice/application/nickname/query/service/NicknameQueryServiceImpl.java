    package com.polywave.userservice.application.nickname.query.service;

import com.polywave.userservice.application.nickname.generator.RandomNicknameGenerator;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

    @Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NicknameQueryServiceImpl implements NicknameQueryService {

    private final UserQueryRepository userQueryRepository;
    private final NicknamePolicyService nicknamePolicyService;
    private final RandomNicknameGenerator randomNicknameGenerator;

    @Override
    public NicknameAvailabilityResult isNicknameAvailable(String nickname) {

        boolean available = !userQueryRepository.existsByNickname(nickname);

        // 금칙어 체크
        if (nicknamePolicyService.isForbidden(nickname)) {
            return new NicknameAvailabilityResult(false);
        }

        return new NicknameAvailabilityResult(available);
    }

    @Override
    public RandomNicknameResult generateRandomNickname() {
        String nickname = randomNicknameGenerator.generate();
        return RandomNicknameResult.of(nickname);
    }
}
