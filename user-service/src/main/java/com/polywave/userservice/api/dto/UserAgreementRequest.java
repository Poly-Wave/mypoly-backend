package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "사용자 약관 동의 저장 요청")
public record UserAgreementRequest(
        @Schema(
                description = "약관 동의 목록 (최소 1개)",
                example = "[{\"termId\":1,\"agreed\":true},{\"termId\":2,\"agreed\":false}]"
        )
        @Valid
        @NotEmpty
        List<TermsAgreementRequest> termAgreements
) {
}
