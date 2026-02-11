package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.api.spec.TermsApi;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController implements TermsApi {

        private final TermsQueryService termsQueryService;

        @Override
        public ResponseEntity<ApiResponse<TermsListResponse>> getLatestTerms() {
                List<TermsResult> termsResults = termsQueryService.getLatestTerms();
                return ResponseEntity.ok(
                                ApiResponse.ok("약관 목록 조회 성공", TermsListResponse.from(termsResults)));
        }

        @Override
        public ResponseEntity<ApiResponse<TermsResponse>> getTermsMeta(Long termsId) {
                TermsResult termsResult = termsQueryService.getTermsMeta(termsId);
                return ResponseEntity.ok(
                                ApiResponse.ok("약관 메타 조회 성공", TermsResponse.from(termsResult)));
        }

        @Override
        public ResponseEntity<String> getTermsHtml(Long termsId) {
                return ResponseEntity
                                .ok()
                                .contentType(MediaType.TEXT_HTML)
                                .body(termsQueryService.getTermsHtml(termsId));
        }
}
