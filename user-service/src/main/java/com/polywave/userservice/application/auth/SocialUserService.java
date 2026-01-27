package com.polywave.userservice.application.auth;

import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.UserOauthRepository;
import com.polywave.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialUserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;

    @Transactional
    public SocialUserResult loginOrRegister(SocialLoginCommand command) {
        UserOauth userOauth = userOauthRepository
                .findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                .orElseGet(() -> createSocialUser(command));

        User user = userOauth.getUser();

        return new SocialUserResult(
                user.getId(),
                userOauth.getProvider(),
                userOauth.getProviderUserId(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }

    private UserOauth createSocialUser(SocialLoginCommand command) {
        User user = userRepository.save(User.builder()
                .nickname(command.nickname())
                .profileImageUrl(command.profileImageUrl())
                .build());

        try {
            return userOauthRepository.save(UserOauth.builder()
                    .provider(command.provider())
                    .providerUserId(command.providerUserId())
                    .user(user)
                    .build());

        } catch (DataIntegrityViolationException e) {
            userRepository.delete(user);

            return userOauthRepository.findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                    .orElseThrow(() -> e);
        }
    }
}
