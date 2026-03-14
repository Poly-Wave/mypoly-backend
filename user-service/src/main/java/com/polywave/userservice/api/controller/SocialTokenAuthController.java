package com.polywave.userservice.api.controller;

import com.polywave.security.JwtUtil;
import com.polywave.security.RefreshJwtUtil;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.dto.SocialTokenSignupRequest;
import com.polywave.userservice.api.dto.TokenRefreshRequest;
import com.polywave.userservice.api.dto.TokenRefreshResponse;
import com.polywave.userservice.api.spec.SocialTokenAuthApi;
import com.polywave.userservice.application.auth.SocialTokenAuthService;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenSignupCommand;
import com.polywave.userservice.application.auth.result.SocialLoginResult;
import com.polywave.userservice.application.session.AuthSessionService;
import com.polywave.userservice.application.userterms.command.TermsAgreement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SocialTokenAuthController implements SocialTokenAuthApi {

        private final SocialTokenAuthService socialTokenAuthService;
        private final AuthSessionService authSessionService;

        private final JwtUtil jwtUtil;
        private final RefreshJwtUtil refreshJwtUtil;

        @Override
        public ResponseEntity<SocialLoginResponse> loginWithToken(String provider,
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
                                result.jwt(),
                                result.refreshToken());
                return ResponseEntity.ok(data);
        }

        @Override
        public ResponseEntity<SocialLoginResponse> signupWithToken(String provider,
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
                                result.jwt(),
                                result.refreshToken());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(data);
        }

        @Override
        public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
                String refreshToken = request.refreshToken();

                if (!refreshJwtUtil.validateRefreshToken(refreshToken)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                Long userId = refreshJwtUtil.extractUserId(refreshToken);
                String sessionId = refreshJwtUtil.extractSessionId(refreshToken);

                // refresh token도 현재 유효 세션과 일치해야만 access token 재발급 허용
                // -> 다른 기기 로그인 후 기존 기기의 refresh 재사용 차단
                if (!authSessionService.isValidSession(userId, sessionId)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                // 현재 정책은 refresh 시 sid를 회전시키지 않고 유지한다.
                String newJwt = jwtUtil.createToken(userId, sessionId);

                return ResponseEntity.ok(new TokenRefreshResponse(newJwt));
        }
}