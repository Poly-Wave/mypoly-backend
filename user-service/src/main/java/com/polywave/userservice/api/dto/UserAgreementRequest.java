package com.polywave.userservice.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UserAgreementRequest(
        @Valid
        @NotEmpty
        List<TermsAgreementRequest> termAgreements
) {
}
