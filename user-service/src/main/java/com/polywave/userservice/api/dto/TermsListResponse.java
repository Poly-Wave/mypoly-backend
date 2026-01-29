package com.polywave.userservice.api.dto;

import java.util.List;

public record TermsListResponse(
        List<TermsResponse> terms
) {
}