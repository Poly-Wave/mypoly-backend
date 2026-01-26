package com.polywave.userservice.service.command;

import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.UserRepository;
import com.polywave.userservice.repository.UserOauthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class SocialUserService {
    private static final Logger log = LoggerFactory.getLogger(SocialUserService.class);
    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;

    /**
     * 소셜 로그인(카카오/구글/애플 등) 성공 시 회원정보 저장/조회
     * @param provider 소셜 제공자 (예: "kakao", "google", "apple")
     * @param providerUserId 소셜 제공자별 유저 ID
     * @param nickname 닉네임(필요시)
     * @param profileImageUrl 프로필 이미지(필요시)
     * @return UserOauth (User 포함)
     */
    @Transactional
    public UserOauth saveOrGetSocialUser(String provider, String providerUserId, String nickname, String profileImageUrl) {
        log.info("[DEBUG] saveOrGetSocialUser called: provider={}, providerUserId={}, nickname={}", provider, providerUserId, nickname);
        Optional<UserOauth> userOauthOpt = userOauthRepository.findByProviderAndProviderUserId(provider, providerUserId);
        if (userOauthOpt.isPresent()) {
            return userOauthOpt.get();
        }
        User user = User.builder()
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .build();
        userRepository.save(user);
        UserOauth userOauth = UserOauth.builder()
            .provider(provider)
            .providerUserId(providerUserId)
            .user(user)
            .build();
        return userOauthRepository.save(userOauth);
    }
}
