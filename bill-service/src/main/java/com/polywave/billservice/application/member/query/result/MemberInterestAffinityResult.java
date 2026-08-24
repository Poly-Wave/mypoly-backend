package com.polywave.billservice.application.member.query.result;

import java.util.List;

/**
 * 의원의 발의 카테고리 분포와, 사용자의 투표 카테고리 분포 사이의 관심사 일치도.
 */
public record MemberInterestAffinityResult(
        double affinityRate,
        List<MemberCategoryRatioResult> topCategories
) {
}
