package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 기본 정보 수정 요청", requiredProperties = {
        "nickname", "gender", "birthDate", "sido", "sigungu", "emdName"
})
public record UserUpdateBasicProfileRequest(

        @NotBlank(message = "별명은 필수입니다.")
        @Size(min = 4, max = 12, message = "별명은 4자 이상 12자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[가-힣])[가-힣0-9]+(?: [가-힣0-9]+)*$",
                message = "별명은 한글/숫자만 사용 가능하며, 공백은 단어 사이에 1개만 허용됩니다.")
        @Schema(description = "별명", example = "홍길동")
        String nickname,

        @NotNull(message = "성별은 필수입니다.")
        @Schema(description = "성별", example = "MAN")
        Gender gender,

        @NotBlank(message = "생년월일은 필수입니다.")
        @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리 숫자여야 합니다.")
        @Schema(description = "생년월일(YYYYMMDD)", example = "19921123")
        String birthDate,

        @NotBlank(message = "시/도는 필수입니다.")
        @Schema(description = "거주지역 시/도", example = "서울특별시")
        String sido,

        @NotBlank(message = "시/군/구는 필수입니다.")
        @Schema(description = "거주지역 시/군/구", example = "강남구")
        String sigungu,

        @NotBlank(message = "읍/면/동은 필수입니다.")
        @Schema(description = "거주지역 읍/면/동", example = "역삼동")
        String emdName
) {
}