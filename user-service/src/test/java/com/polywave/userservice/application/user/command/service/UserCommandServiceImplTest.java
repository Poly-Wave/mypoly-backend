package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.common.exception.InvalidOnboardingStatusException;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.Gender;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Mock
    private UserCommandRepository userCommandRepository;

    @Test
    @DisplayName("유저 프로필 업데이트 성공 - CATEGORY 상태일 때만 가능하며, 완료 시 COMPLETE 상태로 변경된다.")
    void updateUserProfile_Success() {
        // given
        Long userId = 1L;
        UserUpdateProfileCommand command = new UserUpdateProfileCommand(
                Gender.MAN,
                "19900101",
                "서울특별시",
                "강남구",
                "역삼동");
        User user = createTestUser(userId, OnBoardingStatus.CATEGORY);
        given(userCommandRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userCommandService.updateUserProfile(userId, command);

        // then
        assertThat(user.getGender()).isEqualTo(Gender.MAN);
        assertThat(user.getSido()).isEqualTo("서울특별시");
        assertThat(user.getSigungu()).isEqualTo("강남구");
        assertThat(user.getEmdName()).isEqualTo("역삼동");
        assertThat(user.getAddress()).isEqualTo("서울특별시 강남구 역삼동");
        assertThat(user.getOnboardingStatus()).isEqualTo(OnBoardingStatus.COMPLETE);
    }

    @Test
    @DisplayName("유저 프로필 업데이트 실패 - 유저가 존재하지 않음")
    void updateUserProfile_UserNotFound() {
        // given
        Long userId = 999L;
        UserUpdateProfileCommand command = new UserUpdateProfileCommand(
                Gender.MAN, "19900101", "서울", "강남구", "역삼동");
        given(userCommandRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userCommandService.updateUserProfile(userId, command))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 프로필 업데이트 실패 - 현재 온보딩 상태가 CATEGORY가 아님")
    void updateUserProfile_InvalidOnboardingStatus() {
        // given
        Long userId = 1L;
        UserUpdateProfileCommand command = new UserUpdateProfileCommand(
                Gender.MAN, "19900101", "서울", "강남구", "역삼동");
        User user = createTestUser(userId, OnBoardingStatus.ONBOARDING); // CATEGORY가 아님
        given(userCommandRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userCommandService.updateUserProfile(userId, command))
                .isInstanceOf(InvalidOnboardingStatusException.class);
    }

    @Test
    @DisplayName("온보딩 상태 업데이트 성공")
    void updateUserOnboardingStatus_Success() {
        // given
        Long userId = 1L;
        User user = createTestUser(userId, OnBoardingStatus.ONBOARDING);
        given(userCommandRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userCommandService.updateUserOnboardingStatus(userId, OnBoardingStatus.CATEGORY);

        // then
        assertThat(user.getOnboardingStatus()).isEqualTo(OnBoardingStatus.CATEGORY);
    }

    private User createTestUser(Long id, OnBoardingStatus status) {
        User user = User.builder()
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "onboardingStatus", status);
        return user;
    }
}
