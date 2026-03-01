package com.polywave.userservice.api.dto;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * 약관 상세(본문 포함) 응답 DTO
 * - WebView 표시 등 본문이 필요한 경우에만 사용
 */
@Schema(description = "약관 상세 응답(본문 포함)", requiredProperties = {"id", "name", "title", "version", "required", "content", "effectiveFrom"})
public record TermsDetailResponse(
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

        @Schema(description = "약관 본문(HTML)", example = "<h1>서비스 이용약관</h1>...")
        String content,

        @Schema(description = "적용 시작일", example = "2026-01-01")
        LocalDate effectiveFrom
) {
    public static TermsDetailResponse from(TermsDetailResult terms) {
        return new TermsDetailResponse(
                terms.id(),
                terms.name(),
                terms.title(),
                terms.version(),
                terms.required(),
                terms.content(),
                terms.effectiveFrom()
        );
    }
}
