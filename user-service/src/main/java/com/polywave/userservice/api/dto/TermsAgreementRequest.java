package com.polywave.userservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TermsAgreementRequest(
        @NotBlank
        Long termId,
        @NotBlank
        Boolean agreed // true = 동의, false = 철회
) {
}
