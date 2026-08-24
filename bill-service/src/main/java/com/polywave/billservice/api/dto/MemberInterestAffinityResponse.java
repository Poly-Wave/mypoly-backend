package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.member.query.result.MemberInterestAffinityResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = """
        로그인 사용자와 의원의 관심 분야 일치도.

        - 의원 비율: 의원의 전체 발의 건수 중 각 카테고리가 차지하는 비율
        - 사용자 비율: 사용자의 전체 투표 건수 중 각 카테고리가 차지하는 비율
        - 일치도 = 카테고리마다 두 비율 중 작은 값을 취해 모두 합한 값
        - 의원의 발의 이력이나 사용자의 투표 이력이 없으면 일치도는 0.0 입니다.
        """)
public record MemberInterestAffinityResponse(
        @Schema(description = "관심 분야 일치도(0.0~1.0), 소수점 3자리. 화면에는 100을 곱해 % 로 표기", example = "0.7")
        double affinityRate,

        @Schema(description = "의원의 발의 비율이 높은 상위 4개 카테고리(비율 내림차순)")
        List<MemberCategoryRatioResponse> topCategories
) {

    public static MemberInterestAffinityResponse from(MemberInterestAffinityResult result) {
        return new MemberInterestAffinityResponse(
                result.affinityRate(),
                result.topCategories().stream()
                        .map(MemberCategoryRatioResponse::from)
                        .toList()
        );
    }
}
