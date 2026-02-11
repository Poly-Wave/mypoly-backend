package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "닉네임 설정 요청")
public record NicknameCreateRequest(
        @Schema(description = "설정할 닉네임", example = "당근도사")
        @NotBlank
        @Size(min = 4, max = 12)
        @Pattern(
                regexp = "^(?=.*[가-힣])[가-힣0-9]+(?: [가-힣0-9]+)*$",
                message = "닉네임은 한글/숫자만 사용 가능하며, 공백은 단어 사이에 1개만 허용됩니다."
        )
        String nickname
) {
}
