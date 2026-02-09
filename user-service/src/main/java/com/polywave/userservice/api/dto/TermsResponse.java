package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import java.time.LocalDate;

/**
 * 약관 목록(최신 버전) 응답 DTO
 * - 목록에서는 content(본문)를 내려주지 않음
 */
public record TermsResponse(
        Long id,
        String name,
        String title,
        Integer version,
        Boolean required,
        LocalDate effectiveFrom
) {
    public static TermsResponse from(TermsResult result) {
        return new TermsResponse(
                result.id(),
                result.name(),
                result.title(),
                result.version(),
                result.required(),
                result.effectiveFrom()
        );
    }
}
