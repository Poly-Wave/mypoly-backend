package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * 약관 메타데이터 응답 DTO
 * - 목록/상세 메타(/terms, /terms/{id})에서는 content(본문)를 내려주지 않음
 */
@Schema(description = "약관 메타데이터 응답")
public record TermsResponse(
        @Schema(description = "약관 ID", example = "1")
        Long id,

        @Schema(description = "약관 이름(내부 식별자)", example = "SERVICE_TERMS")
        String name,

        @Schema(description = "표시 제목", example = "서비스 이용약관")
        String title,

        @Schema(description = "버전", example = "1")
        Integer version,

        @Schema(description = "필수 여부", example = "true")
        Boolean required,

        @Schema(description = "적용 시작일", example = "2026-01-01")
        LocalDate effectiveFrom
) {
    public static TermsResponse from(TermsResult termsResult) {
        return new TermsResponse(
                termsResult.id(),
                termsResult.name(),
                termsResult.title(),
                termsResult.version(),
                termsResult.required(),
                termsResult.effectiveFrom()
        );
    }
}
