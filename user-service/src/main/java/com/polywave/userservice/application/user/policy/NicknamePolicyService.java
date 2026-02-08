package com.polywave.userservice.application.user.policy;

import com.polywave.userservice.repository.query.NicknameForbiddenWordQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NicknamePolicyService {

    private final NicknameForbiddenWordQueryRepository forbiddenWordQueryRepository;

    public void validate(String nickname) {
        if (forbiddenWordQueryRepository.containsForbiddenWord(nickname)) {
            throw new IllegalStateException("금칙어가 포함된 닉네임입니다.");
        }
    }

    public boolean isForbidden(String nickname) {
        return forbiddenWordQueryRepository.containsForbiddenWord(nickname);
    }
}
