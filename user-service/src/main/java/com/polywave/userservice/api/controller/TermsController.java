package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/terms")
public class TermsController {

    private final TermsQueryService termsQueryService;

    public TermsController(TermsQueryService termsQueryService) {
        this.termsQueryService = termsQueryService;
    }

    @Operation(summary = "최신 약관 목록 조회", description = "최신 버전 약관 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<TermsListResponse> getLatestTerms() {
        List<TermsResult> latestTerms = termsQueryService.getLatestTerms();
        return ApiResponse.ok("약관 목록 조회 성공", TermsListResponse.from(latestTerms));
    }

    @Operation(summary = "약관 메타데이터 조회(JSON)", description = "약관의 메타데이터만 조회합니다. (content 제외)")
    @GetMapping("/{termsId}")
    public ApiResponse<TermsResponse> getTermsMeta(@PathVariable Long termsId) {
        return ApiResponse.ok("약관 메타 조회 성공", termsQueryService.getTermsMeta(termsId));
    }

    @Operation(summary = "약관 HTML 조회(WebView)", description = "WebView 전용 HTML을 text/html로 반환합니다.")
    @GetMapping(value = "/{termsId}/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getTermsHtml(@PathVariable Long termsId) {
        String html = termsQueryService.getTermsHtml(termsId);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
