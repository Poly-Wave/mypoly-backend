package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import java.time.LocalDate;

/**
 * 약관 상세(본문 포함) 응답 DTO
 * - WebView 표시 등 본문이 필요한 경우에만 사용
 */
public record TermsDetailResponse(
        Long id,
        String name,
        String title,
        Integer version,
        Boolean required,
        String content,
        LocalDate effectiveFrom
) {
    public static TermsDetailResponse from(TermsDetailResult terms) {
        return new TermsDetailResponse(
                terms.id(),
                terms.name(),
                terms.title(),
                terms.version(),
                terms.required(),
                terms.content(),
                terms.effectiveFrom()
        );
    }
}
