package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Terms", description = "약관 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {

    private static final String EXAMPLE_TERMS_LIST_OK = """
            {
              "success": true,
              "message": "약관 목록 조회 성공",
              "data": {
                "terms": [
                  {
                    "id": 1,
                    "name": "SERVICE_TERMS",
                    "title": "서비스 이용약관",
                    "version": 1,
                    "required": true,
                    "effectiveFrom": "2026-01-01"
                  }
                ]
              }
            }
            """;

    private static final String EXAMPLE_TERMS_META_OK = """
            {
              "success": true,
              "message": "약관 메타 조회 성공",
              "data": {
                "id": 1,
                "name": "SERVICE_TERMS",
                "title": "서비스 이용약관",
                "version": 1,
                "required": true,
                "effectiveFrom": "2026-01-01"
              }
            }
            """;

    private static final String EXAMPLE_TERMS_NOT_FOUND = """
            {
              "success": false,
              "message": "약관을 찾을 수 없습니다. termsId=999",
              "data": null
            }
            """;

    private final TermsQueryService termsQueryService;

    @Operation(
            summary = "최신 버전 약관 목록 조회",
            description = """
                    최신 버전 기준으로 약관 메타데이터 목록을 조회합니다.

                    - 본문(content)은 포함하지 않습니다.
                    - 본문이 필요하면 `GET /terms/{termsId}/html`을 사용하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_TERMS_LIST_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<TermsListResponse>> getLatestTerms() {
        List<TermsResult> termsResults = termsQueryService.getLatestTerms();
        return ResponseEntity.ok(
                ApiResponse.ok("약관 목록 조회 성공", TermsListResponse.from(termsResults))
        );
    }

    @Operation(
            summary = "약관 메타데이터 단건 조회",
            description = """
                    약관 ID로 약관의 메타데이터를 조회합니다.

                    - 본문(content)은 포함하지 않습니다.
                    - 본문이 필요하면 `GET /terms/{termsId}/html`을 사용하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_TERMS_META_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "약관을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "404 예시", value = EXAMPLE_TERMS_NOT_FOUND)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/{termsId}")
    public ResponseEntity<ApiResponse<TermsResponse>> getTermsMeta(@PathVariable Long termsId) {
        TermsResult termsResult = termsQueryService.getTermsMeta(termsId);
        return ResponseEntity.ok(
                ApiResponse.ok("약관 메타 조회 성공", TermsResponse.from(termsResult))
        );
    }

    @Operation(
            summary = "약관 본문(HTML) 조회",
            description = """
                    약관의 본문을 **HTML(text/html)** 로 반환합니다.

                    - WebView/브라우저 표시 용도
                    - 응답은 JSON(ApiResponse)이 아니라 순수 HTML 문자열입니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공(HTML 반환)",
                    content = @Content(
                            mediaType = "text/html",
                            examples = @ExampleObject(name = "HTML 예시", value = "<h1>서비스 이용약관</h1>...")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "약관을 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping(value = "/{termsId}/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getTermsHtml(@PathVariable Long termsId) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(termsQueryService.getTermsHtml(termsId));
    }
}
