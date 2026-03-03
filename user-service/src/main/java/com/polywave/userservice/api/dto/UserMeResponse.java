package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.Gender;
import com.polywave.userservice.domain.OnBoardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 조회 응답")
public record UserMeResponse(
        @Schema(description = "사용자 ID", example = "2")
        Long userId,

        @Schema(description = "소셜 provider", example = "dev")
        String provider,

        @Schema(description = "소셜 provider user id", example = "swagger")
        String providerUserId,

        @Schema(description = "닉네임", example = "dev-swagger", nullable = true)
        String nickname,

        @Schema(description = "온보딩 상태", example = "SIGNUP", nullable = true)
        OnBoardingStatus onboardingStatus,

        @Schema(description = "성별", example = "MALE", nullable = true)
        Gender gender,

        @Schema(description = "생년월일(YYYYMMDD)", example = "19990101", nullable = true)
        String birthDate,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
        String profileImageUrl,

        @Schema(description = "시/도", example = "서울", nullable = true)
        String sido,

        @Schema(description = "시/군/구", example = "강남구", nullable = true)
        String sigungu,

        @Schema(description = "읍/면/동", example = "역삼동", nullable = true)
        String emdName,

        @Schema(description = "상세 주소", example = "서울 강남구 테헤란로 ...", nullable = true)
        String address
) {
    public static UserMeResponse of(
            Long userId,
            String provider,
            String providerUserId,
            String nickname,
            OnBoardingStatus onboardingStatus,
            Gender gender,
            String birthDate,
            String profileImageUrl,
            String sido,
            String sigungu,
            String emdName,
            String address
    ) {
        return new UserMeResponse(
                userId, provider, providerUserId,
                nickname, onboardingStatus,
                gender, birthDate, profileImageUrl,
                sido, sigungu, emdName, address
        );
    }
}