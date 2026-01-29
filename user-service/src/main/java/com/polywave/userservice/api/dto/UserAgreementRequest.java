package com.polywave.userservice.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UserAgreementRequest(
        @NotEmpty
        Long userId,
        @NotEmpty
        List<TermsAgreementRequest> termAgreements
) {
}
