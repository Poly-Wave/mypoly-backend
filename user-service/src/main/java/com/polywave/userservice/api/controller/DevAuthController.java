package com.polywave.userservice.api.controller;

import com.polywave.security.JwtUtil;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.spec.DevAuthApi;
import com.polywave.userservice.application.auth.SocialUserService;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev-auth")
@ConditionalOnProperty(name = "social.dev-auth.enabled", havingValue = "true")
public class DevAuthController implements DevAuthApi {

    private final String expectedDevKey;
    private final SocialUserService socialUserService;
    private final JwtUtil jwtUtil;

    public DevAuthController(
            @Value("${social.dev-auth.key:}") String expectedDevKey,
            SocialUserService socialUserService,
            JwtUtil jwtUtil) {
        this.expectedDevKey = expectedDevKey;
        this.socialUserService = socialUserService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<SocialLoginResponse> devLogin(String devKey) {
        if (expectedDevKey == null || expectedDevKey.isBlank()) {
            throw new AccessDeniedException("dev-auth key가 설정되지 않았습니다.");
        }
        if (!expectedDevKey.equals(devKey)) {
            throw new AccessDeniedException("dev-auth key가 일치하지 않습니다.");
        }

        SocialUserResult user = socialUserService.login(new SocialLoginCommand(
                "dev",
                "swagger",
                null,
                null));

        String jwt = jwtUtil.createToken(user.userId());

        SocialLoginResponse data = new SocialLoginResponse(
                user.userId(),
                user.provider(),
                user.providerUserId(),
                user.nickname(),
                user.profileImageUrl(),
                jwt);

        return ResponseEntity.ok(data);
    }
}
