package com.polywave.billservice.application.member.query.result;

/**
 * 카테고리 단위 집계 건수. 의원의 발의 건수 집계와 사용자의 투표 건수 집계에 공통으로 쓰인다.
 */
public record CategoryCountResult(
        Long categoryId,
        String categoryCode,
        String categoryName,
        String backgroundColor,
        long count
) {
}
