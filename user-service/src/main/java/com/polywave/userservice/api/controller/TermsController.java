package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {

    private final TermsQueryService termsQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<TermsListResponse>> getTerms() {
        List<TermsResult> results = termsQueryService.getTerms();

        List<TermsResponse> terms = results.stream()
                .map(TermsResponse::from)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "약관 목록 조회 성공",
                        new TermsListResponse(terms)
                )
        );
    }


}
