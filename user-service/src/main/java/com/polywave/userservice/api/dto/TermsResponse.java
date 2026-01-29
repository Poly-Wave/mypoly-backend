package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;

/* TermsResult 값을 controller 반환값으로 사용할 record */
public record TermsResponse(
        Long id,
        String name,
        Boolean required
) {
    public static TermsResponse from(TermsResult result) {
        return new TermsResponse(
                result.id(),
                result.name(),
                result.required()
        );
    }
}