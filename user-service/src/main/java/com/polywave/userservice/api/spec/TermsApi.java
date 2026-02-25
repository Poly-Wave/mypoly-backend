package com.polywave.userservice.api.spec;

import com.polywave.userservice.api.dto.TermsListResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.userservice.api.example.TermsApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Terms", description = "약관 조회 API")
public interface TermsApi {

        @Operation(summary = "최신 버전 약관 목록 조회", description = """
                        최신 버전 기준으로 약관 메타데이터 목록을 조회합니다.

                        - 본문(content)은 포함하지 않습니다.
                        - 본문이 필요하면 `GET /terms/{termsId}/html`을 사용하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = TermsApiExamples.EXAMPLE_TERMS_LIST_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping
        ResponseEntity<TermsListResponse> getLatestTerms();

        @Operation(summary = "약관 메타데이터 단건 조회", description = """
                        약관 ID로 약관의 메타데이터를 조회합니다.

                        - 본문(content)은 포함하지 않습니다.
                        - 본문이 필요하면 `GET /terms/{termsId}/html`을 사용하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = TermsApiExamples.EXAMPLE_TERMS_META_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "약관을 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "404 예시", value = TermsApiExamples.EXAMPLE_TERMS_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/{termsId}")
        ResponseEntity<TermsResponse> getTermsMeta(@PathVariable Long termsId);

        @Operation(summary = "약관 본문(HTML) 조회", description = """
                        약관의 본문을 **HTML(text/html)** 로 반환합니다.

                        - WebView/브라우저 표시 용도
                        - 응답은 JSON(ApiResponse)이 아니라 순수 HTML 문자열입니다.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공(HTML 반환)", content = @Content(mediaType = "text/html", examples = @ExampleObject(name = "HTML 예시", value = "<h1>서비스 이용약관</h1>..."))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "약관을 찾을 수 없음", content = @Content(mediaType = "application/json")),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json"))
        })
        @GetMapping(value = "/{termsId}/html", produces = MediaType.TEXT_HTML_VALUE)
        ResponseEntity<String> getTermsHtml(@PathVariable Long termsId);
}
