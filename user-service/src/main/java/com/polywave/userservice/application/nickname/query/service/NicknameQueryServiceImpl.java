package com.polywave.userservice.application.nickname.query.service;

import com.polywave.userservice.application.nickname.generator.RandomNicknameGenerator;
import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityStatus;
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
    public NicknameAvailabilityResult isNicknameAvailable(String rawNickname) {
        String nickname = NicknameNormalizer.normalize(rawNickname);

        if (nicknamePolicyService.isForbidden(nickname)) {
            return new NicknameAvailabilityResult(false, NicknameAvailabilityStatus.FORBIDDEN);
        }

        boolean available = !userQueryRepository.existsByNickname(nickname);
        return new NicknameAvailabilityResult(available,
                available ? NicknameAvailabilityStatus.AVAILABLE : NicknameAvailabilityStatus.DUPLICATED);
    }

    @Override
    public RandomNicknameResult generateRandomNickname() {
        String nickname = randomNicknameGenerator.generate();
        return RandomNicknameResult.of(nickname);
    }
}
