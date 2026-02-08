package com.polywave.userservice.application.nickname.policy;

import com.polywave.userservice.repository.query.NicknameForbiddenWordQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NicknamePolicyService {

    private final NicknameForbiddenWordQueryRepository forbiddenWordQueryRepository;

    public boolean isForbidden(String nickname) {
        return forbiddenWordQueryRepository.containsForbiddenWord(nickname);
    }
}
