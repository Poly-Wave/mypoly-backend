package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {

    private final TermsQueryService termsQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<TermsListResponse>> getLatestTerms() {
        List<TermsResult> termsResults = termsQueryService.getLatestTerms();
        return ResponseEntity.ok(
                ApiResponse.ok("약관 목록 조회 성공", TermsListResponse.from(termsResults))
        );
    }

    @GetMapping("/{termsId}")
    public ResponseEntity<ApiResponse<TermsResponse>> getTermsMeta(@PathVariable Long termsId) {
        TermsResult termsResult = termsQueryService.getTermsMeta(termsId);
        return ResponseEntity.ok(
                ApiResponse.ok("약관 메타 조회 성공", TermsResponse.from(termsResult))
        );
    }

    @GetMapping(value = "/{termsId}/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getTermsHtml(@PathVariable Long termsId) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(termsQueryService.getTermsHtml(termsId));
    }
}
