package com.polywave.userservice.application.auth;

import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserOauthCommandRepository;
import com.polywave.userservice.repository.query.UserOauthQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialUserService {

    private final UserCommandRepository userCommandRepository;
    private final UserOauthCommandRepository userOauthCommandRepository;
    private final UserOauthQueryRepository userOauthQueryRepository;

    @Transactional(readOnly = true)
    public SocialUserResult login(SocialLoginCommand command) {
        UserOauth userOauth = userOauthQueryRepository
                .findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND")); // 클라이언트가 400 또는 특정 코드로 분기처리할 수 있도록
                                                                                    // 예외 투척

        User user = userOauth.getUser();

        return new SocialUserResult(
                user.getId(),
                userOauth.getProvider(),
                userOauth.getProviderUserId(),
                user.getNickname(),
                user.getProfileImageUrl());
    }

    @Transactional
    public SocialUserResult register(SocialLoginCommand command) {
        userOauthQueryRepository
                .findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("USER_ALREADY_EXISTS");
                });

        UserOauth userOauth = createSocialUser(command);
        User user = userOauth.getUser();

        return new SocialUserResult(
                user.getId(),
                userOauth.getProvider(),
                userOauth.getProviderUserId(),
                user.getNickname(),
                user.getProfileImageUrl());
    }

    private UserOauth createSocialUser(SocialLoginCommand command) {
        User user = userCommandRepository.save(User.builder()
                .nickname(command.nickname())
                .profileImageUrl(command.profileImageUrl())
                .build());

        try {
            return userOauthCommandRepository.save(UserOauth.builder()
                    .provider(command.provider())
                    .providerUserId(command.providerUserId())
                    .user(user)
                    .build());

        } catch (DataIntegrityViolationException e) {
            userCommandRepository.delete(user);

            // 이미 다른 트랜잭션에서 (provider, providerUserId)로 생성했을 수 있으니 재조회
            return userOauthQueryRepository
                    .findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                    .orElseThrow(() -> e);
        }
    }
}
