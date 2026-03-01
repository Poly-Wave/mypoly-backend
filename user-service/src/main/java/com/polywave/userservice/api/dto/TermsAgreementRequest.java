package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "약관 동의 항목", requiredProperties = {"termId", "agreed"})
public record TermsAgreementRequest(
        @Schema(description = "약관 ID", example = "1")
        @NotNull
        Long termId,

        @Schema(description = "동의 여부 (true = 동의, false = 미동의)", example = "true")
        @NotNull
        Boolean agreed
) {
}
