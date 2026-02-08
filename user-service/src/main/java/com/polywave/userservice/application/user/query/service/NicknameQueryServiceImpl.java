    package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.generator.RandomNicknameGenerator;
import com.polywave.userservice.application.user.policy.NicknamePolicyService;
import com.polywave.userservice.application.user.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.user.query.result.RandomNicknameResult;
import com.polywave.userservice.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.random.RandomGenerator;

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

        //  중복 체크
        return new NicknameAvailabilityResult(available);
    }

    @Override
    public RandomNicknameResult generateRandomNickname() {
        String nickname = randomNicknameGenerator.generate();
        return RandomNicknameResult.of(nickname);
    }
}
