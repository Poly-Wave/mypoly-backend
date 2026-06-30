package com.polywave.userservice.application.auth;

import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.common.exception.RejoinBlockedException;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserOauthCommandRepository;
import com.polywave.userservice.repository.query.UserOauthQueryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import com.polywave.common.exception.BusinessException;
import com.polywave.userservice.common.exception.UserErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialUserService {

    /** 탈퇴 후 동일 소셜 계정의 재가입을 차단하는 기간(일). */
    private static final long REJOIN_BLOCK_DAYS = 7;

    private final UserCommandRepository userCommandRepository;
    private final UserOauthCommandRepository userOauthCommandRepository;
    private final UserOauthQueryRepository userOauthQueryRepository;

    @Transactional(readOnly = true)
    public SocialUserResult login(SocialLoginCommand command) {
        UserOauth userOauth = userOauthQueryRepository
                .findByProviderAndProviderUserId(command.provider(), command.providerUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        User user = userOauth.getUser();

        // 탈퇴(WITHDRAWN) 회원은 로그인 불가 — 재가입(signup) 경로로 유도한다.
        if (user.isWithdrawn()) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        return toResult(user, userOauth);
    }

    @Transactional
    public SocialUserResult register(SocialLoginCommand command) {
        Optional<UserOauth> existing = userOauthQueryRepository
                .findByProviderAndProviderUserId(command.provider(), command.providerUserId());

        if (existing.isPresent()) {
            return registerExisting(existing.get(), command);
        }

        UserOauth userOauth = createSocialUser(command);
        return toResult(userOauth.getUser(), userOauth);
    }

    /**
     * 이미 (provider, providerUserId) 매핑이 존재하는 경우의 가입 처리.
     * - ACTIVE 회원   : 이미 가입됨 → USER_ALREADY_EXISTS
     * - WITHDRAWN 회원: 탈퇴 후 7일 이내 → REJOIN_BLOCKED / 7일 경과 → 같은 uid 재활성
     */
    private SocialUserResult registerExisting(UserOauth userOauth, SocialLoginCommand command) {
        User user = userOauth.getUser();

        if (!user.isWithdrawn()) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        if (isWithinRejoinBlock(user)) {
            throw new RejoinBlockedException();
        }

        // 7일 경과 — 같은 uid 재활성(온보딩은 signup 흐름에서 처음부터 다시 진행).
        user.reactivate(command.nickname());
        return toResult(user, userOauth);
    }

    private boolean isWithinRejoinBlock(User user) {
        Instant withdrawnAt = user.getWithdrawnAt();
        return withdrawnAt != null
                && withdrawnAt.isAfter(Instant.now().minus(REJOIN_BLOCK_DAYS, ChronoUnit.DAYS));
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

    private SocialUserResult toResult(User user, UserOauth userOauth) {
        return new SocialUserResult(
                user.getId(),
                userOauth.getProvider(),
                userOauth.getProviderUserId(),
                user.getNickname(),
                user.getProfileImageUrl());
    }
}
