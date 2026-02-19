package com.polywave.userservice.api.spec;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.example.AuthApiExamples;
import com.polywave.userservice.api.example.CommonApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Auth", description = "로그인/JWT 발급 API")
public interface DevAuthApi {

    @Operation(
            summary = "[DEV/LOCAL] Swagger 테스트용 JWT 발급",
            description = """
                    앱 없이 Swagger만으로 보호 API들을 테스트하기 위한 엔드포인트입니다.
                    - social.dev-auth.enabled=true 일 때만 활성화되는 걸 권장합니다.
                    - `X-DEV-KEY` 헤더가 일치해야 합니다.

                    ✅ 사용 순서
                    1) `POST /dev-auth/login` 호출 → 응답의 `data.jwt` 복사
                    2) Swagger 우측 상단 Authorize → `Bearer {jwt}` 입력
                    3) 잠금 아이콘 API 호출
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "JWT 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = AuthApiExamples.EXAMPLE_DEV_AUTH_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "DEV 키 설정/불일치",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "DEV 키 미설정 예시", value = AuthApiExamples.EXAMPLE_DEV_AUTH_KEY_NOT_SET),
                                    @ExampleObject(name = "DEV 키 불일치 예시", value = AuthApiExamples.EXAMPLE_DEV_AUTH_KEY_MISMATCH)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @PostMapping("/login")
    ResponseEntity<ApiResponse<SocialLoginResponse>> devLogin(
            @Parameter(
                    in = ParameterIn.HEADER,
                    description = "DEV 키 (local 기본값: local-dev-key, 배포 시 DEV_AUTH_KEY로 주입)",
                    example = "local-dev-key",
                    required = true
            )
            @RequestHeader("X-DEV-KEY") String devKey
    );
}
