package com.polywave.userservice.application.nickname.command.service;

import com.polywave.userservice.api.exception.DuplicateNicknameException;
import com.polywave.userservice.api.exception.ForbiddenNicknameException;
import com.polywave.userservice.api.exception.UserNotFoundException;
import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
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
        String nickname = NicknameNormalizer.normalize(rawNickname);

        if (nicknamePolicyService.isForbidden(nickname)) {
            throw new ForbiddenNicknameException();
        }

        if (userQueryRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }

        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changeNickname(nickname);
    }
}
