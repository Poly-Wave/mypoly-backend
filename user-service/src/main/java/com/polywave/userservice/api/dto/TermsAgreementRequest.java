package com.polywave.userservice.api.dto;

import jakarta.validation.constraints.NotNull;

public record TermsAgreementRequest(
        @NotNull
        Long termId,
        @NotNull
        Boolean agreed // true = 동의, false = 미동의
) {
}
