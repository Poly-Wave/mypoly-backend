package com.polywave.userservice.application.auth;

import com.polywave.security.JwtUtil;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.security.token.SocialTokenVerifierResolver;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialTokenAuthService {

    private final SocialTokenVerifierResolver verifierResolver;
    private final SocialUserService socialUserService;
    private final JwtUtil jwtUtil;

    @Transactional
    public SocialLoginResponse login(SocialTokenLoginCommand command) {
        SocialVerifiedUser verified = verifierResolver
                .resolve(command.provider(), command.tokenType())
                .verify(command.token(), command.tokenType());

        SocialUserResult user = socialUserService.loginOrRegister(new SocialLoginCommand(
                verified.provider(),
                verified.providerUserId(),
                null,
                verified.profileImageUrl()
        ));

        String jwt = jwtUtil.createToken(user.userId());

        return new SocialLoginResponse(
                user.userId(),
                user.provider(),
                user.providerUserId(),
                user.nickname(),
                user.profileImageUrl(),
                jwt
        );
    }
}
