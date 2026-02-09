package com.polywave.userservice.application.nickname.command.service;

import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.common.exception.DuplicateNicknameException;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NicknameCommandServiceImpl implements NicknameCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final NicknamePolicyService nicknamePolicyService;

    @Override
    public void assignNickname(Long userId, String rawNickname) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String nickname = NicknameNormalizer.normalize(rawNickname);

        // 금칙어 체크
        if (nicknamePolicyService.isForbidden(nickname)) {
            throw new ForbiddenNicknameException();
        }

        // 같은 닉네임이면 idempotent 처리 (그냥 성공으로 봄)
        if (nickname != null && nickname.equals(user.getNickname())) {
            return;
        }

        // 중복 체크
        if (userQueryRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }

        user.changeNickname(nickname);
        userCommandRepository.save(user);
    }
}
