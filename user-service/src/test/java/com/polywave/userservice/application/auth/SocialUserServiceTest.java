package com.polywave.userservice.application.auth;

import com.polywave.common.exception.BusinessException;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.common.exception.UserErrorCode;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserOauthCommandRepository;
import com.polywave.userservice.repository.query.UserOauthQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialUserServiceTest {

    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private UserOauthCommandRepository userOauthCommandRepository;

    @Mock
    private UserOauthQueryRepository userOauthQueryRepository;

    @InjectMocks
    private SocialUserService socialUserService;

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        SocialLoginCommand command = new SocialLoginCommand("KAKAO", "12345", null, null);
        User user = User.builder().nickname("테스트유저").profileImageUrl("img_url").build();
        UserOauth userOauth = UserOauth.builder().provider("KAKAO").providerUserId("12345").user(user).build();

        given(userOauthQueryRepository.findByProviderAndProviderUserId("KAKAO", "12345"))
                .willReturn(Optional.of(userOauth));

        // when
        SocialUserResult result = socialUserService.login(command);

        // then
        assertThat(result.provider()).isEqualTo("KAKAO");
        assertThat(result.providerUserId()).isEqualTo("12345");
        assertThat(result.nickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("로그인 실패 - 없는 유저인 경우 USER_NOT_FOUND (404) BusinessException 예외 발생")
    void login_Fail_UserNotFound() {
        // given
        SocialLoginCommand command = new SocialLoginCommand("KAKAO", "12345", null, null);
        given(userOauthQueryRepository.findByProviderAndProviderUserId("KAKAO", "12345"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> socialUserService.login(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("회원가입(등록) 성공")
    void register_Success() {
        // given
        SocialLoginCommand command = new SocialLoginCommand("GOOGLE", "67890", "구글유저", "img_url");
        given(userOauthQueryRepository.findByProviderAndProviderUserId("GOOGLE", "67890"))
                .willReturn(Optional.empty());

        User savedUser = User.builder().nickname("구글유저").profileImageUrl("img_url").build();
        given(userCommandRepository.save(any(User.class))).willReturn(savedUser);

        UserOauth savedOauth = UserOauth.builder().provider("GOOGLE").providerUserId("67890").user(savedUser).build();
        given(userOauthCommandRepository.save(any(UserOauth.class))).willReturn(savedOauth);

        // when
        SocialUserResult result = socialUserService.register(command);

        // then
        assertThat(result.provider()).isEqualTo("GOOGLE");
        assertThat(result.nickname()).isEqualTo("구글유저");
        verify(userCommandRepository).save(any(User.class));
        verify(userOauthCommandRepository).save(any(UserOauth.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입된 유저인 경우 USER_ALREADY_EXISTS (400) BusinessException 발생")
    void register_Fail_UserAlreadyExists() {
        // given
        SocialLoginCommand command = new SocialLoginCommand("GOOGLE", "67890", "구글유저", null);
        UserOauth existingOauth = UserOauth.builder().provider("GOOGLE").providerUserId("67890").build();

        given(userOauthQueryRepository.findByProviderAndProviderUserId("GOOGLE", "67890"))
                .willReturn(Optional.of(existingOauth));

        // when & then
        assertThatThrownBy(() -> socialUserService.register(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.USER_ALREADY_EXISTS.getCode());
    }

    @Test
    @DisplayName("회원가입 중복 저장 예외(DataIntegrityViolationException) 복구 - 다른 트랜잭션 선입력 상황 방어 로직 검증")
    void register_Concurrent_DataIntegrityViolation_Recover() {
        // given
        SocialLoginCommand command = new SocialLoginCommand("APPLE", "00000", "애플유저", null);

        given(userOauthQueryRepository.findByProviderAndProviderUserId("APPLE", "00000"))
                // 1. 첫 조회 시엔 없다고 판단
                .willReturn(Optional.empty())
                // 3. 예외 발생 후 catch 블록에서 재조회 시엔 존재한다고 리턴
                .willReturn(Optional.of(UserOauth.builder().provider("APPLE").providerUserId("00000")
                        .user(User.builder().nickname("먼저가입한애플유저").build()).build()));

        User savedUser = User.builder().nickname("애플유저").build();
        given(userCommandRepository.save(any(User.class))).willReturn(savedUser);

        // 2. Oauth save 시 DB 유니크 제약 튕김으로 가정
        given(userOauthCommandRepository.save(any(UserOauth.class)))
                .willThrow(new DataIntegrityViolationException("Unique constraint violation"));

        // when
        SocialUserResult result = socialUserService.register(command);

        // then
        // 복구된 기존 데이터 리턴 확인
        assertThat(result.provider()).isEqualTo("APPLE");
        assertThat(result.nickname()).isEqualTo("먼저가입한애플유저");
        // 중복 방지를 위해 삽입했던 User 쓰레기 데이터는 delete를 호출하여 지우는지 확인
        verify(userCommandRepository).delete(savedUser);
    }
}
