package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.member.query.result.MemberCategoryRatioResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의원의 관심 분야(발의 카테고리) 비율")
public record MemberCategoryRatioResponse(
        @Schema(description = "카테고리 코드", example = "REAL_ESTATE")
        String categoryCode,

        @Schema(description = "카테고리명", example = "부동산")
        String categoryName,

        @Schema(description = "카테고리 배경색 HEX, # 제외", example = "E2EDFF")
        String backgroundColor,

        @Schema(description = "의원의 전체 발의 건수 중 이 카테고리 비율(0.0~1.0), 소수점 3자리", example = "0.4")
        double ratio
) {

    public static MemberCategoryRatioResponse from(MemberCategoryRatioResult result) {
        return new MemberCategoryRatioResponse(
                nullToEmpty(result.categoryCode()),
                nullToEmpty(result.categoryName()),
                nullToEmpty(result.backgroundColor()),
                result.ratio()
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
