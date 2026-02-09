package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.Terms;
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
    public static TermsDetailResponse from(Terms terms) {
        return new TermsDetailResponse(
                terms.getId(),
                terms.getName(),
                terms.getTitle(),
                terms.getVersion(),
                terms.getRequired(),
                terms.getContent(),
                terms.getEffectiveFrom()
        );
    }
}
