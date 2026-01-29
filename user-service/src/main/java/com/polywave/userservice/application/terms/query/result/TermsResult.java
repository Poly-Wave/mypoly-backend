package com.polywave.userservice.application.terms.query.result;

/* Service 약관 조회 결과 */
public record TermsResult(
        Long id,
        String name,
        Boolean required
) {
}