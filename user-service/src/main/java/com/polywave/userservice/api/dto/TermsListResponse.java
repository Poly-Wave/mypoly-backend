package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "약관 목록 응답", requiredProperties = {"terms"})
public record TermsListResponse(
        @ArraySchema(
                schema = @Schema(implementation = TermsResponse.class),
                arraySchema = @Schema(description = "약관 목록")
        )
        List<TermsResponse> terms
) {
    public static TermsListResponse from(List<TermsResult> results) {
        return new TermsListResponse(
                results.stream().map(TermsResponse::from).toList()
        );
    }
}
