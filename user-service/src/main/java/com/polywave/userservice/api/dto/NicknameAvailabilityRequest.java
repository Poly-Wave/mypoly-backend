package com.polywave.userservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NicknameAvailabilityRequest(
        @NotBlank
        @Size(min = 4, max = 12)
        @Pattern(regexp = "^[가-힣0-9 ]+$", message = "닉네임은 한글, 숫자, 공백만 가능합니다.")
        String nickname
) {}