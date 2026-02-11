package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "사용자 간략 정보 수정 요청")
public record UserUpdateProfileRequest(
                @NotNull(message = "성별은 필수입니다.") @Schema(description = "성별", example = "MAN, WOMAN") Gender gender,

                @NotBlank(message = "생년월일은 필수입니다.") @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리 숫자여야 합니다.") @Schema(description = "생년월일(YYYYMMDD)", example = "19990101") String birthDate,

                @NotBlank(message = "시/도는 필수입니다.") @Schema(description = "거주지역 시/도", example = "서울특별시") String sido,

                @NotBlank(message = "시/군/구는 필수입니다.") @Schema(description = "거주지역 시/군/구", example = "강남구") String sigungu,

                @NotBlank(message = "읍/면/동은 필수입니다.") @Schema(description = "거주지역 읍/면/동", example = "역삼동") String emdName) {
}
