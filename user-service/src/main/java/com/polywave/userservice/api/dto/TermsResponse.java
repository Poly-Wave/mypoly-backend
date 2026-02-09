package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.Terms;
import java.time.LocalDate;

/**
 * 약관 메타데이터 응답 DTO
 * - 목록/상세 메타(/terms, /terms/{id})에서는 content(본문)를 내려주지 않음
 */
public record TermsResponse(
        Long id,
        String name,
        String title,
        Integer version,
        Boolean required,
        LocalDate effectiveFrom
) {

    public static TermsResponse from(TermsResult termsResult) {
        return new TermsResponse(
                termsResult.id(),
                termsResult.name(),
                termsResult.title(),
                termsResult.version(),
                termsResult.required(),
                termsResult.effectiveFrom()
        );
    }

    public static TermsResponse from(Terms terms) {
        return new TermsResponse(
                terms.getId(),
                terms.getName(),
                terms.getTitle(),
                terms.getVersion(),
                terms.getRequired(),
                terms.getEffectiveFrom()
        );
    }
}
