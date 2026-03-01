package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "통합 회원가입 요청 정보 (소셜 토큰 + 닉네임 + 약관 동의)", requiredProperties = {"tokenType", "token", "nickname", "termAgreements"})
public record SocialTokenSignupRequest(
        @Schema(description = "토큰 타입", example = "ACCESS_TOKEN")
        @NotNull(message = "tokenType은 필수입니다.")
        SocialTokenType tokenType,

        @Schema(description = "SDK로 획득한 소셜 로그인 토큰 문자열", example = "eyJhbGciOi...")
        @NotBlank(message = "token은 필수입니다.")
        String token,

        @Schema(description = "설정할 닉네임", example = "폴리웨이브123")
        @NotBlank
        @Size(min = 4, max = 12)
        @Pattern(
                regexp = "^(?=.*[가-힣])[가-힣0-9]+(?: [가-힣0-9]+)*$",
                message = "닉네임은 한글/숫자만 사용 가능하며, 공백은 단어 사이에 1개만 허용됩니다."
        )
        String nickname,

        @Schema(
                description = "약관 동의 목록 (최소 1개)",
                example = "[{\"termId\":1,\"agreed\":true},{\"termId\":2,\"agreed\":false}]"
        )
        @Valid
        @NotEmpty
        List<TermsAgreementRequest> termAgreements) {
}
