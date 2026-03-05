package com.polywave.userservice.application.nickname.query.service;

import com.polywave.userservice.application.nickname.generator.RandomNicknameGenerator;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityStatus;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.repository.query.UserQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NicknameQueryServiceImplTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private NicknamePolicyService nicknamePolicyService;

    @Mock
    private RandomNicknameGenerator randomNicknameGenerator;

    @InjectMocks
    private NicknameQueryServiceImpl nicknameQueryService;

    @Test
    @DisplayName("닉네임 사용 가능 여부 조회 - 금지어인 경우 바로 FORBIDDEN 반환")
    void isNicknameAvailable_Forbidden() {
        // given
        String inputNickname = " badNickname  "; // 앞뒤 공백은 정규화됨
        String normalized = "badNickname";
        given(nicknamePolicyService.isForbidden(normalized)).willReturn(true);

        // when
        NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(inputNickname);

        // then
        assertThat(result.available()).isFalse();
        assertThat(result.status()).isEqualTo(NicknameAvailabilityStatus.FORBIDDEN);

        // 금지어인 경우 DB 중복 검사까지 가지 않아야 함
        verify(userQueryRepository, never()).existsByNickname(anyString());
    }

    @Test
    @DisplayName("닉네임 사용 가능 여부 조회 - 이미 존재하는 닉네임인 경우 DUPLICATED 반환")
    void isNicknameAvailable_Duplicated() {
        // given
        String inputNickname = "duplicatedName";
        given(nicknamePolicyService.isForbidden(inputNickname)).willReturn(false);
        given(userQueryRepository.existsByNickname(inputNickname)).willReturn(true);

        // when
        NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(inputNickname);

        // then
        assertThat(result.available()).isFalse();
        assertThat(result.status()).isEqualTo(NicknameAvailabilityStatus.DUPLICATED);
    }

    @Test
    @DisplayName("닉네임 사용 가능 여부 조회 - 정상적으로 사용 가능한 경우 AVAILABLE 반환")
    void isNicknameAvailable_Success() {
        // given
        String inputNickname = "goodName";
        given(nicknamePolicyService.isForbidden(inputNickname)).willReturn(false);
        given(userQueryRepository.existsByNickname(inputNickname)).willReturn(false);

        // when
        NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(inputNickname);

        // then
        assertThat(result.available()).isTrue();
        assertThat(result.status()).isEqualTo(NicknameAvailabilityStatus.AVAILABLE);
    }

    @Test
    @DisplayName("랜덤 닉네임 생성 성공")
    void generateRandomNickname_Success() {
        // given
        String generatedNick = "형용사_명사_조합";
        given(randomNicknameGenerator.generate()).willReturn(generatedNick);

        // when
        RandomNicknameResult result = nicknameQueryService.generateRandomNickname();

        // then
        assertThat(result.nickname()).isEqualTo(generatedNick);
    }
}
