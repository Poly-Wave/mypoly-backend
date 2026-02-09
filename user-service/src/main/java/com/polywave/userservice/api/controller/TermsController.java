package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import com.polywave.userservice.domain.Terms;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public ApiResponse<TermsListResponse> getLatestTerms() {
        List<TermsResult> termsResults = termsQueryService.getLatestTerms();
        return ApiResponse.ok("약관 목록 조회 성공", TermsListResponse.from(termsResults));
    }

    @GetMapping("/{termsId}")
    public ApiResponse<TermsResponse> getTermsMeta(@PathVariable Long termsId) {
        Terms terms = termsQueryService.getTerms(termsId);
        return ApiResponse.ok("약관 메타 조회 성공", TermsResponse.from(terms));
    }

    @GetMapping(value = "/{termsId}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String getTermsHtml(@PathVariable Long termsId) {
        return termsQueryService.getTermsHtml(termsId);
    }
}
