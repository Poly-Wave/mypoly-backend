package com.polywave.userservice.application.session;

import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.query.UserQueryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthSessionServiceImpl implements AuthSessionService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional
    public String issueNewSession(Long userId) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String sessionId = UUID.randomUUID().toString();
        user.updateAuthSessionId(sessionId);
        return sessionId;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidSession(Long userId, String sessionId) {
        return userQueryRepository.existsValidSession(userId, sessionId);
    }
}