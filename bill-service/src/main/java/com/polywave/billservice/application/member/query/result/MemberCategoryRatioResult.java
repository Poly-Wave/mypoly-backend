package com.polywave.billservice.application.member.query.result;

/**
 * 의원의 전체 발의 건수 중 특정 카테고리가 차지하는 비율.
 */
public record MemberCategoryRatioResult(
        String categoryCode,
        String categoryName,
        String backgroundColor,
        double ratio
) {
}
