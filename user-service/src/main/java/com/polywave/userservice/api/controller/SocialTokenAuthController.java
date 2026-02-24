package com.polywave.userservice.api.controller;

import com.polywave.common.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.dto.SocialTokenSignupRequest;
import com.polywave.userservice.api.spec.SocialTokenAuthApi;
import com.polywave.userservice.application.auth.SocialTokenAuthService;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenSignupCommand;
import com.polywave.userservice.application.auth.result.SocialLoginResult;
import com.polywave.userservice.application.userterms.command.TermsAgreement;
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
        public ResponseEntity<ApiResponse<SocialLoginResponse>> loginWithToken(String provider,
                                                                               SocialTokenLoginRequest request) {
                SocialTokenLoginCommand command = new SocialTokenLoginCommand(
                                provider,
                                request.tokenType(),
                                request.token());

                SocialLoginResult result = socialTokenAuthService.login(command);
                SocialLoginResponse data = new SocialLoginResponse(
                                result.userId(),
                                result.provider(),
                                result.providerUserId(),
                                result.nickname(),
                                result.profileImageUrl(),
                                result.jwt());
                return ResponseEntity.ok(ApiResponse.ok("소셜 로그인 성공", data));
        }

        @Override
        public ResponseEntity<ApiResponse<SocialLoginResponse>> signupWithToken(String provider,
                        SocialTokenSignupRequest request) {
                SocialTokenSignupCommand command = new SocialTokenSignupCommand(
                                provider,
                                request.tokenType(),
                                request.token(),
                                request.nickname(),
                                request.termAgreements().stream()
                                                .map(ta -> new TermsAgreement(ta.termId(), ta.agreed()))
                                                .toList());

                SocialLoginResult result = socialTokenAuthService.signup(command);
                SocialLoginResponse data = new SocialLoginResponse(
                                result.userId(),
                                result.provider(),
                                result.providerUserId(),
                                result.nickname(),
                                result.profileImageUrl(),
                                result.jwt());
                return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                                .body(ApiResponse.ok("소셜 회원가입 성공", data));
        }
}
