package com.polywave.userservice.api.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Auth", description = "소셜 로그인 시작(리다이렉트) 관련 API")
public interface AuthStartApi {

    @Operation(summary = "소셜 로그인 시작(302 리다이렉트)", description = """
            이 API는 OAuth2 로그인 플로우를 시작시키는 **리다이렉트 엔드포인트**입니다. (302 + Location)

            Swagger UI의 Execute는 브라우저 fetch 기반이라 리다이렉트를 따라가며,
            외부 도메인(카카오)으로 넘어가는 과정에서 CORS/쿠키 이슈로 중간 302가 깔끔히 보이지 않을 수 있습니다.

            확인/테스트 방법
            - 브라우저 주소창에서 직접 `/users/auth/kakao` 접속
            - 또는 `curl -v`로 Location 헤더를 확인

            로그인 성공 시
            최종적으로 서버가 JSON으로 JWT를 내려줍니다(OAuth2 success handler가 응답).
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "OAuth2 Authorization Endpoint로 리다이렉트", headers = @Header(name = "Location", description = "예: /users/oauth2/authorization/kakao")),
            @ApiResponse(responseCode = "404", description = "지원하지 않는 provider")
    })
    @GetMapping("/{provider}")
    ResponseEntity<Void> start(
            @Parameter(description = "현재 지원 provider", example = "kakao") @PathVariable String provider,
            HttpServletRequest request);
}
