package com.polywave.userservice.application.auth;

import com.polywave.security.JwtUtil;
import com.polywave.security.RefreshJwtUtil;
import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenSignupCommand;
import com.polywave.userservice.application.auth.result.SocialLoginResult;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityStatus;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.application.userterms.command.service.UserTermsCommandService;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.security.token.SocialTokenVerifier;
import com.polywave.userservice.security.token.SocialTokenVerifierResolver;
import com.polywave.userservice.security.token.SocialVerifiedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SocialTokenAuthServiceTest {

        @Mock
        private SocialTokenVerifierResolver verifierResolver;

        @Mock
        private SocialTokenVerifier socialTokenVerifier;

        @Mock
        private SocialUserService socialUserService;

        @Mock
        private NicknameQueryService nicknameQueryService;

        @Mock
        private UserTermsCommandService userTermsCommandService;

        @Mock
        private UserCommandService userCommandService;

        @Mock
        private JwtUtil jwtUtil;

        @Mock
        private RefreshJwtUtil refreshJwtUtil;

        @InjectMocks
        private SocialTokenAuthService socialTokenAuthService;

        @Test
        @DisplayName("로그인 - 토큰 검증 및 JWT 발급 성공")
        void login_Success() {
                // given
                SocialTokenLoginCommand command = new SocialTokenLoginCommand("KAKAO", SocialTokenType.ACCESS_TOKEN,
                                "VALID_TOKEN");
                SocialVerifiedUser verifiedUser = new SocialVerifiedUser("KAKAO", "12345", "img_url");
                SocialUserResult socialUserResult = new SocialUserResult(1L, "KAKAO", "12345", "test_nick", "img_url");

                given(verifierResolver.resolve("KAKAO", SocialTokenType.ACCESS_TOKEN)).willReturn(socialTokenVerifier);
                given(socialTokenVerifier.verify("VALID_TOKEN", SocialTokenType.ACCESS_TOKEN)).willReturn(verifiedUser);
                given(socialUserService.login(any(SocialLoginCommand.class))).willReturn(socialUserResult);
                given(jwtUtil.createToken(1L)).willReturn("MOCK_JWT");
                given(refreshJwtUtil.createRefreshToken(1L)).willReturn("MOCK_REFRESH");

                // when
                SocialLoginResult result = socialTokenAuthService.login(command);

                // then
                assertThat(result.userId()).isEqualTo(1L);
                assertThat(result.provider()).isEqualTo("KAKAO");
                assertThat(result.nickname()).isEqualTo("test_nick");
                assertThat(result.jwt()).isEqualTo("MOCK_JWT");
                assertThat(result.refreshToken()).isEqualTo("MOCK_REFRESH");
        }

        @Test
        @DisplayName("회원가입 - 정상적인 정보 제공 시 토큰 발급 및 성공 콜백")
        void signup_Success() {
                // given
                SocialTokenSignupCommand command = new SocialTokenSignupCommand("APPLE", SocialTokenType.ID_TOKEN,
                                "VALID_APPLE_TOKEN", "새로운닉네임", Collections.emptyList());
                SocialVerifiedUser verifiedUser = new SocialVerifiedUser("APPLE", "00000", "apple_img");
                NicknameAvailabilityResult nicknameResult = new NicknameAvailabilityResult(true,
                                NicknameAvailabilityStatus.AVAILABLE);
                SocialUserResult socialUserResult = new SocialUserResult(2L, "APPLE", "00000", "새로운닉네임", "apple_img");

                given(verifierResolver.resolve("APPLE", SocialTokenType.ID_TOKEN)).willReturn(socialTokenVerifier);
                given(socialTokenVerifier.verify("VALID_APPLE_TOKEN", SocialTokenType.ID_TOKEN))
                                .willReturn(verifiedUser);
                given(nicknameQueryService.isNicknameAvailable("새로운닉네임")).willReturn(nicknameResult);
                given(socialUserService.register(any(SocialLoginCommand.class))).willReturn(socialUserResult);
                given(jwtUtil.createToken(2L)).willReturn("MOCK_JWT_APPLE");
                given(refreshJwtUtil.createRefreshToken(2L)).willReturn("MOCK_REFRESH_APPLE");

                // when
                SocialLoginResult result = socialTokenAuthService.signup(command);

                // then
                assertThat(result.userId()).isEqualTo(2L);
                assertThat(result.provider()).isEqualTo("APPLE");
                assertThat(result.nickname()).isEqualTo("새로운닉네임");
                assertThat(result.jwt()).isEqualTo("MOCK_JWT_APPLE");

                verify(userTermsCommandService).saveUserAgreement(any(UserAgreementCommand.class));
                verify(userCommandService).updateUserOnboardingStatus(2L, OnBoardingStatus.SIGNUP);
        }

        @Test
        @DisplayName("회원가입 실패 - 유효하지 않거나 중복된 닉네임일 경우 ForbiddenNicknameException 발생")
        void signup_Fail_ForbiddenNickname() {
                // given
                SocialTokenSignupCommand command = new SocialTokenSignupCommand("GOOGLE", SocialTokenType.ID_TOKEN,
                                "VALID_GOOGLE_TOKEN", "금지어포함닉네임", Collections.emptyList());
                SocialVerifiedUser verifiedUser = new SocialVerifiedUser("GOOGLE", "77777", "google_img");
                NicknameAvailabilityResult nicknameResult = new NicknameAvailabilityResult(false,
                                NicknameAvailabilityStatus.DUPLICATED); // 실패 응답

                given(verifierResolver.resolve("GOOGLE", SocialTokenType.ID_TOKEN)).willReturn(socialTokenVerifier);
                given(socialTokenVerifier.verify("VALID_GOOGLE_TOKEN", SocialTokenType.ID_TOKEN))
                                .willReturn(verifiedUser);
                given(nicknameQueryService.isNicknameAvailable("금지어포함닉네임")).willReturn(nicknameResult);

                // when & then
                assertThatThrownBy(() -> socialTokenAuthService.signup(command))
                                .isInstanceOf(ForbiddenNicknameException.class);

                // 뒷 단계 로직들은 호출되지 않아야 함
                verifyNoInteractions(socialUserService, userTermsCommandService, userCommandService, jwtUtil,
                                refreshJwtUtil);
        }
}
