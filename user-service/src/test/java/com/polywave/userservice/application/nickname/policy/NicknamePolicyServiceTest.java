package com.polywave.userservice.application.nickname.policy;

import com.polywave.userservice.repository.query.NicknameForbiddenWordQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NicknamePolicyServiceTest {

    @Mock
    private NicknameForbiddenWordQueryRepository forbiddenWordQueryRepository;

    @InjectMocks
    private NicknamePolicyService nicknamePolicyService;

    @Test
    @DisplayName("금지어 포함 검사 위임 로직 확인")
    void isForbidden_ReturnsTrueWhenContainsForbiddenWord() {
        // given
        String nickname = "관리자";
        given(forbiddenWordQueryRepository.containsForbiddenWord(nickname)).willReturn(true);

        // when
        boolean result = nicknamePolicyService.isForbidden(nickname);

        // then
        assertThat(result).isTrue();
    }
}
