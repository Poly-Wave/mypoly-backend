package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import java.util.List;

public record TermsListResponse(
        List<TermsResponse> terms
) {
    public static TermsListResponse from(List<TermsResult> results) {
        return new TermsListResponse(
                results.stream().map(TermsResponse::from).toList()
        );
    }
}
