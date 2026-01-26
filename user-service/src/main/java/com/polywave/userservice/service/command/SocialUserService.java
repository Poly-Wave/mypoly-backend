
package com.polywave.userservice.service.command;

import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.UserRepository;
import com.polywave.userservice.repository.UserOauthRepository;
import com.polywave.userservice.api.dto.SocialUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class SocialUserService {

    /**
     * LazyInitializationException 방지용: 트랜잭션 내에서 DTO로 변환
     */
    @Transactional
    public SocialUserDto loginOrRegisterDto(String provider, String providerUserId, String nickname, String profileImageUrl) {
        UserOauth userOauth = loginOrRegister(provider, providerUserId, nickname, profileImageUrl);
        User user = userOauth.getUser();
        return new SocialUserDto(
            user.getId(),
            userOauth.getProvider(),
            userOauth.getProviderUserId(),
            user.getNickname(),
            user.getProfileImageUrl()
        );
    }
    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;
    
    @Transactional
    UserOauth loginOrRegister(String provider, String providerUserId, String nickname, String profileImageUrl) {
        return userOauthRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> createSocialUser(provider, providerUserId, nickname, profileImageUrl));
    }

    private UserOauth createSocialUser(String provider, String providerUserId, String nickname, String profileImageUrl) {
        try {
            User user = userRepository.save(User.builder()
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .build());

            return userOauthRepository.save(UserOauth.builder()
                    .provider(provider)
                    .providerUserId(providerUserId)
                    .user(user)
                    .build());
        } catch (DataIntegrityViolationException e) {
            // 동시 로그인(레이스)로 unique(provider, provider_user_id) 충돌 시, 이미 생성된 레코드 재조회
            Optional<UserOauth> existing = userOauthRepository.findByProviderAndProviderUserId(provider, providerUserId);
            if (existing.isPresent()) return existing.get();
            throw e;
        }
    }
}
