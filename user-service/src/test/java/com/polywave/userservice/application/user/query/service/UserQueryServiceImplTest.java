package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;
import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.application.user.query.service.impl.UserQueryServiceImpl;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.repository.query.UserQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Test
    @DisplayName("온보딩 상태 조회 성공")
    void getOnboardingStatus_Success() {
        // given
        Long userId = 1L;
        given(userQueryRepository.findOnboardingStatusByUserId(userId)).willReturn(OnBoardingStatus.COMPLETE);

        // when
        OnboardingStatusResult result = userQueryService.getOnboardingStatus(userId);

        // then
        assertThat(result.status()).isEqualTo(OnBoardingStatus.COMPLETE);
    }

    @Test
    @DisplayName("온보딩 상태 조회 실패 - 유저 없음")
    void getOnboardingStatus_UserNotFound() {
        // given
        Long userId = 999L;
        given(userQueryRepository.findOnboardingStatusByUserId(userId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> userQueryService.getOnboardingStatus(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("내 정보(UserMe) 조회 성공")
    void getMe_Success() {
        // given
        Long userId = 1L;
        UserMeResult mockResult = new UserMeResult(
                userId, "KAKAO", "123", "nickname", OnBoardingStatus.COMPLETE,
                null, null, "image.png", "서울", "강남구", "역삼동", "서울 강남구 역삼동");
        given(userQueryRepository.findUserMeByUserId(userId)).willReturn(mockResult);

        // when
        UserMeResult result = userQueryService.getMe(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 유저 없음")
    void getMe_UserNotFound() {
        // given
        Long userId = 999L;
        given(userQueryRepository.findUserMeByUserId(userId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> userQueryService.getMe(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
