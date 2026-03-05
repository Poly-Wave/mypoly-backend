package com.polywave.userservice.application.nickname.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NicknameNormalizerTest {

    @ParameterizedTest
    @DisplayName("닉네임 정규화 성공 검증 - 앞뒤 공백 제거 및 연속된 공백 하나로 치환")
    @CsvSource({
            "'  test  ', 'test'",
            "'test   nickname', 'test nickname'",
            "' t e s t ', 't e s t'",
            " , ", // null 인 경우
            "'', ''",
            "'   ', ''"
    })
    void normalize_Success(String input, String expected) {
        // null 처리를 위한 대응
        if (input == null)
            expected = null;

        String result = NicknameNormalizer.normalize(input);

        assertThat(result).isEqualTo(expected);
    }
}
