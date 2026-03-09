package com.polywave.userservice.application.auth;

import com.polywave.security.JwtUtil;
import com.polywave.security.RefreshJwtUtil;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenSignupCommand;
import com.polywave.userservice.application.auth.result.SocialLoginResult;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import com.polywave.userservice.application.session.AuthSessionService;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.application.userterms.command.service.UserTermsCommandService;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.domain.OnBoardingStatus;
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
        private final NicknameQueryService nicknameQueryService;
        private final UserTermsCommandService userTermsCommandService;
        private final UserCommandService userCommandService;
        private final AuthSessionService authSessionService;

        /** Access Token 발급/검증 유틸 */
        private final JwtUtil jwtUtil;

        /** Refresh Token 발급/검증 유틸 */
        private final RefreshJwtUtil refreshJwtUtil;

        @Transactional
        public SocialLoginResult login(SocialTokenLoginCommand command) {
                SocialVerifiedUser verified = verifierResolver
                                .resolve(command.provider(), command.tokenType())
                                .verify(command.token(), command.tokenType());

                SocialUserResult user = socialUserService.login(new SocialLoginCommand(
                                verified.provider(),
                                verified.providerUserId(),
                                null,
                                verified.profileImageUrl()));

                // 로그인 성공 시 새 sid를 발급하여 기존 기기의 세션을 무효화한다.
                String sessionId = authSessionService.issueNewSession(user.userId());

                String jwt = jwtUtil.createToken(user.userId(), sessionId);
                String refreshToken = refreshJwtUtil.createRefreshToken(user.userId(), sessionId);

                return new SocialLoginResult(
                                user.userId(),
                                user.provider(),
                                user.providerUserId(),
                                user.nickname(),
                                user.profileImageUrl(),
                                jwt,
                                refreshToken);
        }

        @Transactional
        public SocialLoginResult signup(SocialTokenSignupCommand command) {
                // 1. 소셜 토큰 검증
                SocialVerifiedUser verified = verifierResolver
                                .resolve(command.provider(), command.tokenType())
                                .verify(command.token(), command.tokenType());

                // 2. 닉네임 유효성/중복 검증
                NicknameAvailabilityResult nicknameResult = nicknameQueryService
                                .isNicknameAvailable(command.nickname());
                if (!nicknameResult.available()) {
                        throw new ForbiddenNicknameException();
                }

                // 3. 소셜 유저 생성 (User, UserOauth)
                SocialUserResult user = socialUserService.register(new SocialLoginCommand(
                                verified.provider(),
                                verified.providerUserId(),
                                command.nickname(),
                                verified.profileImageUrl()));

                // 4. 약관 동의 내역 저장
                UserAgreementCommand agreementCommand = new UserAgreementCommand(user.userId(),
                                command.termsAgreements());
                userTermsCommandService.saveUserAgreement(agreementCommand);

                // 5. 온보딩 상태값 변경
                userCommandService.updateUserOnboardingStatus(user.userId(), OnBoardingStatus.SIGNUP);

                // 6. 토큰 발급 (Access/Refresh)
                // 회원가입 직후도 로그인 완료 상태이므로 새 sid를 발급한다.
                String sessionId = authSessionService.issueNewSession(user.userId());
                String jwt = jwtUtil.createToken(user.userId(), sessionId);
                String refreshToken = refreshJwtUtil.createRefreshToken(user.userId(), sessionId);

                return new SocialLoginResult(
                                user.userId(),
                                user.provider(),
                                user.providerUserId(),
                                user.nickname(),
                                user.profileImageUrl(),
                                jwt,
                                refreshToken);
        }
}