package com.polywave.userservice.application.terms.query.result;

import java.time.LocalDate;

/**
 * 약관 상세 조회 결과 (본문 포함)
 * - WebView(/terms/{id}/html) 등 content가 필요한 경우에만 사용
 */
public record TermsDetailResult(
        Long id,
        String name,
        String title,
        Integer version,
        Boolean required,
        Boolean isMarketing,
        String content,
        LocalDate effectiveFrom
) {
}