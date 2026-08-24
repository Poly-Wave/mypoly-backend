package com.polywave.userservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.dto.SocialTokenSignupRequest;
import com.polywave.userservice.api.dto.TokenRefreshRequest;
import com.polywave.userservice.api.dto.TokenRefreshResponse;
import com.polywave.userservice.api.example.AuthApiExamples;
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

        @Operation(
                        summary = "SDK 토큰 검증 기반 로그인(Access/Refresh 발급)",
                        description = "앱에서 SDK로 소셜 로그인 후 받은 access_token 또는 id_token을 서버에 전달하면, 서버가 검증 후 우리 서비스 Access/Refresh 토큰을 발급합니다."
        )
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "지원하지 않는 provider/tokenType 또는 요청 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "지원하지 않는 소셜 로그인 수단", value = AuthApiExamples.EXAMPLE_UNSUPPORTED_SOCIAL_LOGIN),
                                        @ExampleObject(name = "사용자를 찾을 수 없음", value = AuthApiExamples.EXAMPLE_USER_NOT_FOUND)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "소셜 토큰 검증 실패(만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "인증 실패", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                        }))
        })
        @PostMapping("/token/{provider}/login")
        ResponseEntity<SocialLoginResponse> loginWithToken(
                        @Parameter(description = "kakao/google/apple", example = "kakao")
                        @PathVariable String provider,
                        @Valid @RequestBody SocialTokenLoginRequest request);

        @Operation(summary = "SDK 토큰 검증 기반 회원가입(Access/Refresh 발급)")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 오류/유효하지 않은 닉네임 등", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "잘못된 요청", value = CommonApiExamples.EXAMPLE_BAD_REQUEST)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "소셜 토큰 검증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "인증 실패", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                        }))
        })
        @PostMapping("/token/{provider}/signup")
        ResponseEntity<SocialLoginResponse> signupWithToken(
                        @PathVariable String provider,
                        @Valid @RequestBody SocialTokenSignupRequest request);

        @Operation(
                        summary = "Refresh Token으로 Access Token 재발급",
                        description = "Access Token이 만료(401)된 경우, 앱은 Refresh Token을 사용해 새 Access Token을 발급받습니다."
        )
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh 토큰 검증 실패/만료", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "인증 실패", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                        }))
        })
        @PostMapping("/refresh")
        ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request);
}