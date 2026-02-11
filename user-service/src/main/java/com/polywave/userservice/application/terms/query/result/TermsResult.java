package com.polywave.userservice.application.terms.query.result;

import java.time.LocalDate;

/**
 * 약관 목록(최신 버전) 조회 결과
 * - 목록 API에서는 본문(content)은 제외해서 payload를 가볍게 유지
 */
public record TermsResult(
        Long id,
        String name,
        String title,
        Integer version,
        Boolean required,
        LocalDate effectiveFrom
) {
}
