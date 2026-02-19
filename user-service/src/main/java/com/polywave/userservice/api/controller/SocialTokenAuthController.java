package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.spec.SocialTokenAuthApi;
import com.polywave.userservice.application.auth.SocialTokenAuthService;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SocialTokenAuthController implements SocialTokenAuthApi {

    private final SocialTokenAuthService socialTokenAuthService;

    @Override
    public ResponseEntity<ApiResponse<SocialLoginResponse>> loginWithToken(String provider, SocialTokenLoginRequest request) {
        SocialTokenLoginCommand command = new SocialTokenLoginCommand(
                provider,
                request.tokenType(),
                request.token()
        );

        SocialLoginResponse data = socialTokenAuthService.login(command);
        return ResponseEntity.ok(ApiResponse.ok("소셜 로그인 성공", data));
    }
}
