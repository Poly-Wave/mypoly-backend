package com.polywave.userservice.api.spec;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.UserAgreementRequest;
import com.polywave.userservice.api.example.CommonApiExamples;
import com.polywave.userservice.api.example.UserTermsApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UserTerms", description = "유저 약관 동의(업서트) API")
@SecurityRequirement(name = "bearerAuth")
public interface UserTermsApi {

        @Operation(summary = "약관 동의 저장(업서트)", description = """
                        로그인 사용자의 약관 동의 정보를 저장합니다. (JWT 인증 필요)

                        동작 규칙(업서트)
                        - (user_id, terms_id) 기준으로 **있으면 update**, 없으면 **insert**
                        - 요청에 포함되지 않은 다른 약관 동의 내역은 **삭제하지 않습니다**
                        - 요청 본문에 **중복 termId가 있으면 400** 처리합니다. (DB 유니크 충돌 방지)

                        예외 정책(실제 코드 기준)
                        - 요청 값 검증 실패: 400
                        - 유효하지 않은 약관 ID 포함: 400
                        - 사용자 없음: 404
                        - 인증 필요: 401 / 권한 없음: 403
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserTermsApiExamples.EXAMPLE_CREATED_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 잘못된 termId/중복 termId", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "검증 실패 예시", value = UserTermsApiExamples.EXAMPLE_BAD_REQUEST_VALIDATION),
                                        @ExampleObject(name = "중복 termId 예시", value = UserTermsApiExamples.EXAMPLE_BAD_REQUEST_DUPLICATE_TERM_ID),
                                        @ExampleObject(name = "유효하지 않은 termId 예시", value = UserTermsApiExamples.EXAMPLE_BAD_REQUEST_INVALID_TERM_ID)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "403 예시", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "404 예시", value = UserTermsApiExamples.EXAMPLE_USER_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PostMapping("/agree")
        ResponseEntity<ApiResponse<Void>> agreeToTerms(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "약관 동의 목록(업서트 대상)", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAgreementRequest.class), examples = @ExampleObject(name = "요청 예시", value = UserTermsApiExamples.EXAMPLE_AGREE_REQUEST))) @Valid @RequestBody UserAgreementRequest request,
                        @Parameter(hidden = true) Long userId);
}
