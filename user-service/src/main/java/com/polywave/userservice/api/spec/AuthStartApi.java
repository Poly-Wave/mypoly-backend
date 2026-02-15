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

@Tag(name = "Auth", description = "로그인/JWT 발급 API")
public interface AuthStartApi {

    @Operation(
            summary = "[DEV/LOCAL] 소셜 로그인 시작(302 리다이렉트)",
            description = """
                    dev/local 전용 리다이렉트 로그인 시작점입니다.
                    - 브라우저로 호출하면 `/oauth2/authorization/{provider}` 로 302 이동합니다.
                    - Swagger Execute에서는 리다이렉트 플로우가 깔끔히 보이지 않을 수 있어요.

                    추천(Swagger만으로 테스트):
                    - `POST /dev-auth/login` 으로 JWT 발급 후 Authorize → 보호 API 테스트
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "OAuth2 Authorization Endpoint로 리다이렉트",
                    headers = @Header(name = "Location")
            ),
            @ApiResponse(responseCode = "404", description = "지원하지 않는 provider")
    })
    @GetMapping("/redirect/{provider}")
    ResponseEntity<Void> start(
            @Parameter(
                    description = "provider (dev/local에서만 의미 있음)",
                    example = "kakao"
            )
            @PathVariable String provider,
            HttpServletRequest request
    );
}
