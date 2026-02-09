package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsDetailResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    /**
     * 약관 목록(종류별 최신 버전)
     * - 목록에서는 본문(content)을 내려주지 않음
     */
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

    /**
     * 약관 상세(본문 포함)
     * - WebView로 보여줄 때 이 API를 사용
     */
    @GetMapping("/{termsId}")
    public ResponseEntity<ApiResponse<TermsDetailResponse>> getTermsDetail(@PathVariable Long termsId) {
        TermsDetailResponse detail = termsQueryService.getTermsDetail(termsId);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "약관 상세 조회 성공",
                        detail
                )
        );
    }
}
