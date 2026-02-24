package com.polywave.userservice.api.spec;

import com.polywave.common.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.dto.SocialTokenSignupRequest;
import com.polywave.userservice.api.example.AuthApiExamples;
import com.polywave.userservice.api.example.CommonApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "로그인/JWT 발급 API")
public interface SocialTokenAuthApi {

        @Operation(summary = "SDK 토큰 검증 기반 로그인(JWT 발급)", description = """
                        앱에서 SDK로 소셜 로그인 → access_token 또는 id_token을 받은 뒤,
                        서버에 전달하면 서버가 검증 후 우리 서비스 JWT를 발급합니다.

                        provider별 권장 토큰 타입
                        - kakao: ACCESS_TOKEN
                        - google/apple: (현재 템플릿) ID_TOKEN 위주로 확장 예정

                        Swagger만으로 테스트(추천)
                        - dev/local: `POST /dev-auth/login` 으로 JWT 발급 → Authorize → 보호 API 테스트
                        """)
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "JWT 발급 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = AuthApiExamples.EXAMPLE_SOCIAL_TOKEN_LOGIN_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "지원하지 않는 provider/tokenType 또는 요청 값 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "지원하지 않는 요청 예시", value = AuthApiExamples.EXAMPLE_UNSUPPORTED_SOCIAL_LOGIN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "소셜 토큰 검증 실패(만료/위조/aud 불일치 등)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카카오 토큰 실패 예시", value = AuthApiExamples.EXAMPLE_INVALID_KAKAO_TOKEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PostMapping("/{provider}/token")
        ResponseEntity<ApiResponse<SocialLoginResponse>> loginWithToken(
                        @Parameter(description = "provider", example = "kakao") @PathVariable String provider,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = """
                                        SDK에서 받은 토큰을 전달합니다.
                                        - tokenType: ACCESS_TOKEN | ID_TOKEN
                                        - token: 실제 토큰 문자열
                                        """, required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialTokenLoginRequest.class), examples = {
                                        @ExampleObject(name = "ACCESS_TOKEN 예시(카카오 권장)", value = AuthApiExamples.EXAMPLE_SOCIAL_TOKEN_LOGIN_REQUEST_ACCESS_TOKEN),
                                        @ExampleObject(name = "ID_TOKEN 예시(구글/애플 템플릿)", value = AuthApiExamples.EXAMPLE_SOCIAL_TOKEN_LOGIN_REQUEST_ID_TOKEN)
                        })) @Valid @RequestBody SocialTokenLoginRequest request);

        @Operation(summary = "소셜 토큰 + 약관 + 닉네임 통합 회원가입", description = """
                        SDK로 받은 소셜 토큰(kakao: ACCESS_TOKEN, google/apple: ID_TOKEN)과
                        사용자가 입력한 닉네임, 그리고 필수/선택 약관 동의 내역을 한 번에 서버에 전송합니다.

                        서버 검증(토큰 유효성, 닉네임 중복 등) 후, `User`, `UserOauth`, `UserTerms` 레코드를 생성하고
                        회원가입 완료 및 JWT 액세스 토큰을 반환합니다.

                        참고: `/auth/{provider}/token`으로 로그인 실패 시 반환되는 코드(또는 HTTP 상태 등 클라이언트 협의)를 보고
                        이 API로 분기합니다.
                        """)
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 및 JWT 발급 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "약관 누락, 닉네임 중복/규칙 위반, 유효하지 않은 Provider 등 잘못된 요청"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "소셜 토큰 만료 또는 위조 내역 발견")
        })
        @PostMapping("/{provider}/signup")
        ResponseEntity<ApiResponse<SocialLoginResponse>> signupWithToken(
                        @Parameter(description = "provider (kakao, google, apple 등)", example = "kakao") @PathVariable String provider,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "토큰 타입, 토큰 값, 새로운 닉네임, 동의한 약관 목록의 JSON", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialTokenSignupRequest.class))) @Valid @RequestBody SocialTokenSignupRequest request);
}
