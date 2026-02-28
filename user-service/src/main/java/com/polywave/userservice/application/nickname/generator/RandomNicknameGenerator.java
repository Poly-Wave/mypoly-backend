package com.polywave.userservice.application.nickname.generator;

import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.domain.NicknameWordType;
import com.polywave.userservice.common.exception.UserValidationException;
import com.polywave.userservice.common.exception.UserErrorCode;
import com.polywave.userservice.repository.query.NicknameWordQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class RandomNicknameGenerator {

    private static final int MAX_TRIES = 50;
    private static final int MAX_LENGTH = 12;

    private final NicknameWordQueryRepository nicknameWordQueryRepository;

    public String generate() {
        for (int i = 0; i < MAX_TRIES; i++) {
            String adjective = nicknameWordQueryRepository.pickRandom(NicknameWordType.ADJECTIVE);
            String noun = nicknameWordQueryRepository.pickRandom(NicknameWordType.NOUN);

            String suffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
            String nickname = NicknameNormalizer.normalize(adjective + " " + noun + " " + suffix);

            if (nickname.length() <= MAX_LENGTH) {
                return nickname;
            }
        }

        throw new UserValidationException(UserErrorCode.NICKNAME_GENERATION_FAILED);
    }
}
