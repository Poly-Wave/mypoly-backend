package com.polywave.userservice.application.nickname.generator;

import com.polywave.userservice.domain.NicknameWordType;
import com.polywave.userservice.repository.query.NicknameWordQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class RandomNicknameGenerator {

    private final NicknameWordQueryRepository nicknameWordQueryRepository;

    public String generate() {
        String adjective = nicknameWordQueryRepository.pickRandom(NicknameWordType.ADJECTIVE);
        String noun = nicknameWordQueryRepository.pickRandom(NicknameWordType.NOUN);
        String suffix = String.format(
                "%04d",
                ThreadLocalRandom.current().nextInt(10000)
        );

        return adjective + " " + noun + " " + suffix;
    }
}
